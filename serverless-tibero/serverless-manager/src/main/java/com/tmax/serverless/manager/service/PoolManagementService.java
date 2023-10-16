package com.tmax.serverless.manager.service;

import com.tmax.serverless.core.annotation.Autowired;
import com.tmax.serverless.core.annotation.Service;
import com.tmax.serverless.core.context.DBServerlessMode;
import com.tmax.serverless.manager.context.DBInstance;
import com.tmax.serverless.manager.context.DBInstancePool;
import java.util.HashMap;
import java.util.Map;

@Service
public class PoolManagementService {
  @Autowired
  DBInstancePool dbInstancePool;

  /* 완전히 새로운 DB를 Pool에 추가*/
  public boolean addDBtoInstancePool(String alias, DBServerlessMode mode) {
      Map<String, DBInstance> pool;
      if (!dbInstancePool.isExistInDBPool(alias))
        return false;
      DBInstance newDBInstance = new DBInstance(alias, mode);

      switch (mode) {
        case Active:
          pool = dbInstancePool.getActiveDBPool();
          pool.put(alias, newDBInstance);
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
}
