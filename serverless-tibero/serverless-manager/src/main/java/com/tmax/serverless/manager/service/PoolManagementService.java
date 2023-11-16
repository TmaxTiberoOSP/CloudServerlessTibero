package com.tmax.serverless.manager.service;

import com.tmax.serverless.core.annotation.Autowired;
import com.tmax.serverless.core.annotation.Service;
import com.tmax.serverless.core.context.DBServerlessMode;
import com.tmax.serverless.core.message.admin.AdminMsgAddDB;
import com.tmax.serverless.manager.context.DBInstance;
import com.tmax.serverless.manager.context.DBInstancePool;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class PoolManagementService {
  @Autowired
  KubernetesManagementService kubernetesManagementService;

  @Autowired
  DBInstancePool dbInstancePool;

  public void init() {
    /*
    최초에 Kubernetes API를 통해 pod name 한번 쫙 읽어서
    dbInstancePool 구축
    */
  }

  /* 완전히 새로운 DB를 Pool에 추가*/
  public boolean addDBtoInstancePool(AdminMsgAddDB req) {
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
    DBInstance newDBInstance = new DBInstance(alias, mode);

    switch (mode) {
      case Active:
        pool = dbInstancePool.getActiveDBPool();
        pool.put(alias, newDBInstance);
        log.info("ActivePool:" + pool);
        return true;
      case WarmUp:
        pool = dbInstancePool.getWarmUpDBPool();
        pool.put(alias, newDBInstance);
        return true;
      default:
        return false;
    }
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
      /*
       load balancer 등록 등 일련의 절차
       로드밸런서의 동작은 로드밸런서에 특정 ip를 등록하는 식으로 이루어지는 것이 아닌, 해당 각 노드의 pod의 yaml에
       특정한 Label을 기입함으로써 이루어진다.
       단 이는 pod가 동작중에도 동적으로 수행될 수 있다.
       이를 위해 위의 yaml을 parsing하여 특정할 라벨링을 수정하는 메서드가 필요하다.
       pod에 대한 접근은 다음과 같은 형식으로 가능하다.
       kubectl edit pod pod이름 -n 네임스페이스 이름
      */
      kubernetesManagementService.addDBtoLB(alias);
      /* 만약 아래의 작업이 실패하면, Load balancer에 등록된 것도 삭제해주어야 함
      * 물론 throw 처리할수도 있음
      * */
      result = moveDBtoAnotherPool(dbInstanceEntry.getKey(), DBServerlessMode.WarmUp ,DBServerlessMode.Active);

      if (!result) {
        kubernetesManagementService.removeDBfromLB(alias);
      }

      return result;
    }

    /* 특정 alias를 주느냐 아니냐에 따라 구현이 다름 */
    /*
    public boolean scaleInDB() {
      Map<String, DBInstance> pool = dbInstancePool.getActiveDBPool();
      Map.Entry<String, DBInstance> dbInstanceEntry;

      if (pool.isEmpty())
        return false;

      dbInstanceEntry = pool.entrySet().iterator().next();
      return moveDBtoAnotherPool(dbInstanceEntry.getKey(), DBServerlessMode.Active ,DBServerlessMode.ActiveCold);
    }
  */
  public boolean scaleInDB(String alias) throws IOException {
    Map<String, DBInstance> pool = dbInstancePool.getActiveDBPool();

    if (!pool.containsKey(alias))
      return false;

    /*
      붙을 pod name Parsing
    */
    kubernetesManagementService.removeDBfromLB(alias);
    /*
    kubectl label pod [pod 이름] -n [namespace 이름] [key]=[value] --overwrite
    ex) kubectl label pod zeta-4-69cf67b8c-t65xr -n tibero app=zeta-db --overwrite
    */

    return moveDBtoAnotherPool(alias, DBServerlessMode.Active ,DBServerlessMode.ActiveCold);
  }

  public boolean makeDBWarmUp(String alias) throws IOException {
    Map<String, DBInstance> pool = dbInstancePool.getActiveColdDBPool();

    if (!pool.containsKey(alias))
      return false;

    /*
      kubectl exec -it zeta-0-84bb86bb4c-q95pt -n tibero -- /bin/bash -c "export TB_HOME=/tibero;export TB_SID=tac0;tbdown"
    */
    kubernetesManagementService.executeDBCommand(alias, "tbdown");

    return moveDBtoAnotherPool(alias, DBServerlessMode.ActiveCold ,DBServerlessMode.WarmUp);
  }
}