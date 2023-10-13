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

    public boolean addDBtoInstancePool(String alias, DBServerlessMode mode) {
      Map<String, DBInstance> pool;
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

}
