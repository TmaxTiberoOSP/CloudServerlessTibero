package com.tmax.serverless.manager.service;

import com.tmax.serverless.core.annotation.Autowired;
import com.tmax.serverless.core.annotation.Service;
import com.tmax.serverless.core.context.DBServerlessMode;
import com.tmax.serverless.manager.context.DBInstance;
import com.tmax.serverless.manager.context.DBInstancePool;
import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class PoolManagementService {
  @Autowired
  DBInstancePool dbInstancePool;

  /* 완전히 새로운 DB를 Pool에 추가*/
  public boolean addDBtoInstancePool(String alias, DBServerlessMode mode) {
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

    public boolean scaleOutDB() {
      Map<String, DBInstance> pool = dbInstancePool.getWarmUpDBPool();
      Map.Entry<String, DBInstance> dbInstanceEntry;
      boolean result=false;
      if (pool.isEmpty())
        return false;

      dbInstanceEntry = pool.entrySet().iterator().next();
      /*
       load balance 등록 등 일련의 절차
       */

      /* 만약 아래의 작업이 실패하면, Load balancer에 등록된 것도 삭제해주어야 함
      * 물론 throw 처리할수도 있음
      * */
      result = moveDBtoAnotherPool(dbInstanceEntry.getKey(), DBServerlessMode.WarmUp ,DBServerlessMode.Active);

      if (!result) {
        /* load balacer에서 삭제 */
      }

      return result;
    }

    /* 특정 alias를 주느냐 아니냐에 따라 구현이 다름 */
    public boolean scaleInDB() {
      Map<String, DBInstance> pool = dbInstancePool.getActiveDBPool();
      Map.Entry<String, DBInstance> dbInstanceEntry;

      if (pool.isEmpty())
        return false;

      dbInstanceEntry = pool.entrySet().iterator().next();
      return moveDBtoAnotherPool(dbInstanceEntry.getKey(), DBServerlessMode.Active ,DBServerlessMode.ActiveCold);
    }

  public boolean scaleInDB(String alias) {
    Map<String, DBInstance> pool = dbInstancePool.getActiveDBPool();

    if (!pool.containsKey(alias))
      return false;

    /*
    load balancer에서 삭제하는등의 일련의 작업
    */

    return moveDBtoAnotherPool(alias, DBServerlessMode.Active ,DBServerlessMode.ActiveCold);
  }

  public boolean makeDBWarmUp(String alias) {
    Map<String, DBInstance> pool = dbInstancePool.getActiveColdDBPool();

    if (!pool.containsKey(alias))
      return false;

    /* DB Warmup으로 변경 */

    return moveDBtoAnotherPool(alias, DBServerlessMode.ActiveCold ,DBServerlessMode.WarmUp);
  }
}


