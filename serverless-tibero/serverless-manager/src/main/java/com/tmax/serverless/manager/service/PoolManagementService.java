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
    String id = req.getId();
    String alias = req.getAlias();
    String ip = req.getIp();
    int port = req.getPort();
    String dbUser = req.getDbUser();
    String dbPassword = req.getDbPassword();
    String podName = req.getPodName();
    String nonDeletable = req.getNonDeletable();
    DBServerlessMode mode = req.getMode();

    Map<String, DBInstance> pool;
    if (dbInstancePool.isExistInDBPool(alias))
      return false;

    DBInstance newDBInstance = DBInstance.builder()
        .id(id)
        .dbName(dbName)
        .alias(alias)
        .ip(ip)
        .port(port)
        .dbUser(dbUser)
        .dbPassword(dbPassword)
        .podName(podName)
        .nonDeletable(nonDeletable)
        .mode(mode)
        .build();

    if (!sysMasterService.addDBToSysMaster(newDBInstance)) {
      log.info("Fail to add new DB Instance(%s) to SysMaster!", alias);
      return false;
    }

    switch (mode) {
      case Active:
        pool = dbInstancePool.getActiveDBPool();
        pool.put(alias, newDBInstance);
        log.info("ActivePool:" + alias);
        return true;
      case WarmUp:
        pool = dbInstancePool.getWarmUpDBPool();
        pool.put(alias, newDBInstance);
        log.info("WarmUpPool:" + alias);
        /*최초 등록된 디비가 WarmUP 데이터베이스라면, LoadBalancing 대상에서
         * 제외하고, DB를 Down 시켜준다.*/
        if(!makeDBDown(alias)) {
          pool.remove(alias);

          if (!sysMasterService.deleteDBFromSysMaster(newDBInstance)) {
            new RuntimeException("deleteDBFromSysMaster(id:%s, alias:%s) Failed during adding DB.");
          }
          else {
            log.info("add DB into WarmUpPool failed: " + alias);
            return false;
          }
        }
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
      monitoringList.add(entry.getValue().getId());
    }
    pool = dbInstancePool.getWarmUpDBPool();
    for (Map.Entry<String, DBInstance> entry : pool.entrySet()) {
      monitoringList.add(entry.getValue().getId());
      /*
      Dynamic RSB Remastering이 추가됨에 따라 Scale-in/out 과정에서 DB를 켜거나 종료시키지 않는다.

      if(!kubernetesManagementService.executeDBCommand(entry.getValue(), DBExecuteCommand.Down)) {
        log.info("WarmUp DB initialize fail : DB Down", entry.getValue().getAlias());
        return false;
      }
      */
    }

    if (!sysMasterService.addGroupToSysMaster(groupName, monitoringList))
      return false;

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
    Map<String, DBInstance> activeColdDBPool = dbInstancePool.getActiveColdDBPool();
    Map<String, DBInstance> warmUpDBPool = dbInstancePool.getWarmUpDBPool();
    boolean result=false;
    DBInstance dbInstance;

    if (activeColdDBPool.isEmpty() && warmUpDBPool.isEmpty()) {
      log.info("The Instance Pool is empty.");
      return false;
    }

    log.info("Start to scale out db : " + alias);

    /*ToDo : 구조가 갑자기 바뀌면서 급히 수정하느라 코드가 엉성해짐. 리팩토링 필요*/

    if (!activeColdDBPool.containsKey((alias))) {
      dbInstance = warmUpDBPool.get(alias);
    }
    else {
       dbInstance = activeColdDBPool.get(alias);
    }

    if (!moveDBtoAnotherPool(alias, DBServerlessMode.ActiveCold, DBServerlessMode.Active)) {
      if (!moveDBtoAnotherPool(alias, DBServerlessMode.WarmUp, DBServerlessMode.Active)) {
        log.info("Fail to move DB from WarmUp to Active");
        return false;
      }
    } else {
      log.info("the ActiveCold one goes to Active");
      return kubernetesManagementService.executeLBCommand(dbInstance, LBExecuteCommand.ActiveDB);
    }

    /*
    Dynamic RSB Remastering이 추가됨에 따라 Scale-in/out 과정에서 DB를 켜거나 종료시키지 않는다.

    if (!kubernetesManagementService.executeDBCommand(dbInstance, DBExecuteCommand.Boot)) {
      moveDBtoAnotherPool(alias, DBServerlessMode.Active, DBServerlessMode.WarmUp);
      return false;
    }
    */

    /* ToDo : DB Boot 성공하고 LB 등록실패하면? */
    return kubernetesManagementService.executeLBCommand(dbInstance, LBExecuteCommand.ActiveDB);
  }

  public boolean scaleInDB(String alias) {
    Map<String, DBInstance> pool = dbInstancePool.getActiveDBPool();
    DBInstance dbInstance;

    log.info("Start to scale-in db : " + alias);

    if (!pool.containsKey(alias)) {
      log.info(alias + " is not in Active Instance Pool");
      return false;
    }

    dbInstance = pool.get(alias);

    if (!moveDBtoAnotherPool(alias, DBServerlessMode.Active, DBServerlessMode.ActiveCold)) {
      log.info("Fail to move DB from Active to ActiveCold");
      return false;
    }

    if (!kubernetesManagementService.executeLBCommand(dbInstance, LBExecuteCommand.StandbyDB)) {
      moveDBtoAnotherPool(alias, DBServerlessMode.ActiveCold, DBServerlessMode.Active);
      return false;
    }

    return true;
  }

  public boolean makeDBWarmUp(String alias) {
    Map<String, DBInstance> pool = dbInstancePool.getActiveColdDBPool();
    DBInstance dbInstance;

    log.info("Start to make db WarmUp : " + alias);
    if (!pool.containsKey(alias)) {
      log.info(alias + " is not in ActiveCold Instance Pool");
      return false;
    }

    dbInstance = pool.get(alias);

    if (!moveDBtoAnotherPool(alias, DBServerlessMode.ActiveCold, DBServerlessMode.WarmUp)) {
      log.info("Fail to move DB from ActiveCold to WarmUp");
      return false;
    }

    /*
    Dynamic RSB Remastering이 추가됨에 따라 Scale-in/out 과정에서 DB를 켜거나 종료시키지 않는다.

    if (!kubernetesManagementService.executeDBCommand(dbInstance, DBExecuteCommand.Down)) {
      moveDBtoAnotherPool(alias, DBServerlessMode.WarmUp, DBServerlessMode.ActiveCold);
      return false;
    }
    */

    return true;
  }

  public boolean makeDBDown(String alias) {
    Map<String, DBInstance> pool = dbInstancePool.getWarmUpDBPool();
    DBInstance dbInstance;

    log.info("start DB down : " + alias);
    if (!pool.containsKey(alias)) {
      log.info(alias + " is not in WarmUp Instance Pool");
      return false;
    }

    dbInstance = pool.get(alias);

    /*
    추후 별도의 처리가 가능하도록 아래의 조건문을 남겨두었음.
     */

    /*
    Dynamic RSB Remastering이 추가됨에 따라 Scale-in/out 과정에서 DB를 켜거나 종료시키지 않는다.

    if (kubernetesManagementService.executeLBCommand(dbInstance, LBExecuteCommand.StandbyDB) &&
            kubernetesManagementService.executeDBCommand(dbInstance, DBExecuteCommand.Down)) {
      return true;
    }

    return false;
    */

    return kubernetesManagementService.executeLBCommand(dbInstance, LBExecuteCommand.StandbyDB);
  }
}