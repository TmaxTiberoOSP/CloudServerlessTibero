package com.tmax.serverless.manager.service;

import com.tmax.serverless.core.annotation.Autowired;
import com.tmax.serverless.core.annotation.Service;
import com.tmax.serverless.core.annotation.Value;
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
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.net.URI;
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
    String dbName = req.getDbName();
    String alias = req.getAlias();
    String ip = req.getIp();
    int port = req.getPort();
    String dbUser = req.getDbUser();
    String dbPassword = req.getDbPassword();
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
        .mode(mode)
        .build();

    if (!sysMasterService.addDBToSysMaster(newDBInstance)) {
      log.info("Fail to add new DB Instance(%s) to SysMaster!", alias);
      DBInstance.decreaseId();
      return false;
    }

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

    if (!sysMasterService.addGroupToSysMaster(groupName, monitoringList))
      return false;

    return true;
  }

    /* Pool사이에서의 DB Instance 이동 */
  public boolean moveDBtoAnotherPool(String alias, DBServerlessMode sourceMode, DBServerlessMode targetMode) {
    Map<String, DBInstance> sourcePool;
    Map<String, DBInstance> targetPool;
    DBInstance moveDBInstance;

    if (!dbInstancePool.isExistInDBPool(alias)) {
      return false;
    }

      /* deep copy, shallow copy 고려해야함. 지금은 Flow만 작성 */
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

  public boolean scaleOutDB(String alias) throws IOException {
    Map<String, DBInstance> pool = dbInstancePool.getWarmUpDBPool();
    Map.Entry<String, DBInstance> dbInstanceEntry;
    boolean result=false;
    if (pool.isEmpty())
      return false;

    dbInstanceEntry = pool.entrySet().iterator().next();

    kubernetesManagementService.executeLBCommand(alias, LBExecuteCommand.ActiveDB);

    kubernetesManagementService.executeDBCommand(alias, DBExecuteCommand.Boot);

    result = moveDBtoAnotherPool(dbInstanceEntry.getKey(), DBServerlessMode.WarmUp ,DBServerlessMode.Active);

    if (!result) {
      kubernetesManagementService.executeLBCommand(alias, LBExecuteCommand.StandbyDB);
    }

    return result;
  }

  public boolean scaleInDB(String alias) throws IOException {
    Map<String, DBInstance> pool = dbInstancePool.getActiveDBPool();

    if (!pool.containsKey(alias))
      return false;

    kubernetesManagementService.executeLBCommand(alias, LBExecuteCommand.StandbyDB);

    return moveDBtoAnotherPool(alias, DBServerlessMode.Active ,DBServerlessMode.ActiveCold);
  }

  public boolean makeDBWarmUp(String alias) throws IOException {
    Map<String, DBInstance> pool = dbInstancePool.getActiveColdDBPool();

    if (!pool.containsKey(alias))
      return false;

    kubernetesManagementService.executeDBCommand(alias, DBExecuteCommand.Down);

    return moveDBtoAnotherPool(alias, DBServerlessMode.ActiveCold ,DBServerlessMode.WarmUp);
  }

}