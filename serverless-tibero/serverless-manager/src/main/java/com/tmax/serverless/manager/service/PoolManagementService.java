package com.tmax.serverless.manager.service;

import com.tmax.serverless.core.annotation.Autowired;
import com.tmax.serverless.core.annotation.Service;
import com.tmax.serverless.core.context.DBExecuteCommand;
import com.tmax.serverless.core.context.DBServerlessMode;
import com.tmax.serverless.core.context.LBExecuteCommand;
import com.tmax.serverless.core.message.admin.AdminMsgAddDB;
import com.tmax.serverless.core.message.admin.AdminMsgAddGroup;
import com.tmax.serverless.manager.context.DBInstance;
import com.tmax.serverless.manager.context.DBInstancePool;
import com.tmax.serverless.manager.service.k8s.KubernetesManagementService;
import com.tmax.serverless.manager.service.sysmaster.SysMasterService;
import java.util.ArrayList;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class PoolManagementService {
  @Autowired
  private SysMasterService sysMasterService;
  @Autowired
  private KubernetesManagementService kubernetesManagementService;
  @Autowired
  private DBInstancePool dbInstancePool;

  public void init() {
    /*
    최초에 Kubernetes API를 통해 pod name 한번 쫙 읽어서
    dbInstancePool 구축
    */
  }

  /* 완전히 새로운 DB를 Pool에 추가*/
  public synchronized boolean addDBtoInstancePool(AdminMsgAddDB req) {
    log.info("addDBtoInstancePool req: {}", req);
    String dbName = req.getDbName();
    String alias = req.getAlias();
    String ip = req.getIp();
    int port = req.getPort();
    String dbUser = req.getDbUser();
    String dbPassword = req.getDbPassword();
    String podName = req.getPodName();
    DBServerlessMode mode = req.getMode();

    Map<String, DBInstance> pool;
    if (dbInstancePool.isExistInDBPool(alias))
      return false;

    DBInstance newDBInstance = DBInstance.builder()
        .id(DBInstance.getNewId())
        .dbName(dbName)
        .alias(alias)
        .ip(ip)
        .port(port)
        .dbUser(dbUser)
        .dbPassword(dbPassword)
        .podName(podName)
        .mode(mode)
        .build();

    /*
    if (!sysMasterService.addDBToSysMaster(newDBInstance)) {
      log.info("Fail to add new DB Instance(%s) to SysMaster!", alias);
      DBInstance.decreaseId();
      return false;
    }
    */

    switch (mode) {
      case Active:
        pool = dbInstancePool.getActiveDBPool();
        pool.put(alias, newDBInstance);
        log.info("ActivePool:" + pool);
        return true;
      case WarmUp:
        pool = dbInstancePool.getWarmUpDBPool();
        pool.put(alias, newDBInstance);
        log.info("WarmUpPool:" + pool);
        return true;
      default:
        return false;
    }
  }

  public boolean addGroupForMonitoring(AdminMsgAddGroup req) {
    log.info("addGroupForMonitoring req: {}", req);
    String groupName = req.getGroupName();
    ArrayList<String> monitoringList = new ArrayList<>();
    Map<String, DBInstance> pool;

    pool = dbInstancePool.getActiveDBPool();
    for (Map.Entry<String, DBInstance> entry : pool.entrySet()) {
      monitoringList.add(entry.getValue().getId().toString());
    }
    pool = dbInstancePool.getWarmUpDBPool();
    for (Map.Entry<String, DBInstance> entry : pool.entrySet()) {
      monitoringList.add(entry.getValue().getId().toString());
    }

    /*
    if (!sysMasterService.addGroupToSysMaster(groupName, monitoringList))
      return false;
    */

    return true;
  }

    /* Pool사이에서의 DB Instance 이동 */
  public boolean moveDBtoAnotherPool(String alias, DBServerlessMode sourceMode, DBServerlessMode targetMode) {
    log.info("moveDBtoAnotherPool alias:" + alias + ", sourceMode:" + sourceMode + ", targetMode:" + targetMode);
    Map<String, DBInstance> sourcePool;
    Map<String, DBInstance> targetPool;
    DBInstance moveDBInstance;

    if (!dbInstancePool.isExistInDBPool(alias)) {
      log.info(alias + " is not in a Instance Pool");
      return false;
    }

    /* Todo : deep copy, shallow copy 고려해야함. 지금은 Flow만 작성 */
    synchronized (this) {
      sourcePool = dbInstancePool.getDBPoolByMode(sourceMode);
      targetPool = dbInstancePool.getDBPoolByMode(targetMode);

      if (!sourcePool.containsKey(alias))
        return false;
      moveDBInstance = sourcePool.get(alias);
      targetPool.put(alias, moveDBInstance);
      sourcePool.remove(alias);
    }

    return true;
  }

  public boolean scaleOutDB(String alias) {
    log.info("scaleOutDB alias:" + alias);
    Map<String, DBInstance> activecoldDBPool = dbInstancePool.getActiveColdDBPool();
    Map<String, DBInstance> warmupDBPool = dbInstancePool.getWarmUpDBPool();
    boolean result=false;
    DBInstance dbInstance;

    if (activecoldDBPool.isEmpty() && warmupDBPool.isEmpty()) {
      log.info("The Instance Pool is empty.");
      return false;
    }

    dbInstance = activecoldDBPool.get(alias);
    if (dbInstance == null) {
      dbInstance = warmupDBPool.get(alias);
    }

    log.info("Start to scale out db : " + alias);
    if (!moveDBtoAnotherPool(alias, DBServerlessMode.ActiveCold ,DBServerlessMode.Active)) {
      if (!moveDBtoAnotherPool(alias, DBServerlessMode.WarmUp ,DBServerlessMode.Active)) {
        log.info("Fail to move DB from WarmUp to Active");
        return false;
      }
    } else {
      log.info("the ActiveCold one goes to Active");
      return kubernetesManagementService.executeLBCommand(dbInstance, LBExecuteCommand.ActiveDB);
    }

    if (!kubernetesManagementService.executeDBCommand(dbInstance, DBExecuteCommand.Boot)) {
      moveDBtoAnotherPool(alias, DBServerlessMode.Active, DBServerlessMode.WarmUp);
      return false;
    }

    /* ToDo : DB Boot 성공하고 LB 등록실패하면? */
    return kubernetesManagementService.executeLBCommand(dbInstance, LBExecuteCommand.ActiveDB);
  }

  public boolean scaleInDB(String alias) {
    log.info("scaleInDB alias:" + alias);
    Map<String, DBInstance> pool = dbInstancePool.getActiveDBPool();
    DBInstance dbInstance;

    log.info("Start to scale-in db : " + alias);

    if (!pool.containsKey(alias)) {
      log.info(alias + " is not in Active Instance Pool");
      return false;
    }

    dbInstance = pool.get(alias);

    if (!moveDBtoAnotherPool(alias, DBServerlessMode.Active ,DBServerlessMode.ActiveCold)) {
      log.info("Fail to move DB from Active to ActiveCold");
      return false;
    }

    if (!kubernetesManagementService.executeLBCommand(dbInstance, LBExecuteCommand.StandbyDB)) {
      moveDBtoAnotherPool(alias, DBServerlessMode.ActiveCold ,DBServerlessMode.Active);
      return false;
    }

    return true;
  }

  public boolean makeDBWarmUp(String alias) {
    log.info("makeDBWarmUp alias:" + alias);
    Map<String, DBInstance> pool = dbInstancePool.getActiveColdDBPool();
    DBInstance dbInstance;

    log.info("Start to make db WarmUp : " + alias);
    if (!pool.containsKey(alias)) {
      log.info(alias + " is not in ActiveCold Instance Pool");
      return false;
    }

    dbInstance = pool.get(alias);

    if (!moveDBtoAnotherPool(alias, DBServerlessMode.ActiveCold ,DBServerlessMode.WarmUp)) {
      log.info("Fail to move DB from ActiveCold to WarmUp");
      return false;
    }

    if (!kubernetesManagementService.executeDBCommand(dbInstance, DBExecuteCommand.Down)) {
      moveDBtoAnotherPool(alias, DBServerlessMode.WarmUp, DBServerlessMode.ActiveCold);
      return false;
    }

    return true;
  }
}