package com.tmax.serverless.manager.context;

import com.tmax.serverless.core.annotation.Component;
import java.util.concurrent.ConcurrentHashMap;

import com.tmax.serverless.core.context.DBServerlessMode;
import lombok.Getter;

@Component
public class DBInstancePool {
  @Getter
  private final ConcurrentHashMap<String, DBInstance> activeDBPool = new ConcurrentHashMap<>();
  @Getter
  private final ConcurrentHashMap<String, DBInstance> activeColdDBPool = new ConcurrentHashMap<>();
  @Getter
  private final ConcurrentHashMap<String, DBInstance> warmUpDBPool = new ConcurrentHashMap<>();
  public boolean isExistInDBPool(String alias) {
      return (activeDBPool.containsKey(alias) || activeColdDBPool.containsKey(alias) ||
              warmUpDBPool.containsKey(alias));
  }

  public ConcurrentHashMap<String, DBInstance> getDBPoolByMode(DBServerlessMode mode) {
      switch (mode) {
          case Active:
              return activeDBPool;
          case ActiveCold:
              return activeColdDBPool;
          case WarmUp:
              return warmUpDBPool;
          default:
              return null;
      }
  }
}