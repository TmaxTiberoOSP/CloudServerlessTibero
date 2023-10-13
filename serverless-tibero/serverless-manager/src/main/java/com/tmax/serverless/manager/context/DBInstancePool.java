package com.tmax.serverless.manager.context;

import com.tmax.serverless.core.annotation.Component;
import java.util.concurrent.ConcurrentHashMap;
import lombok.Getter;

@Component
public class DBInstancePool {
  @Getter
  private final ConcurrentHashMap<String, DBInstance> activeDBPool = new ConcurrentHashMap<>();
  @Getter
  private final ConcurrentHashMap<String, DBInstance> activeColdDBPool = new ConcurrentHashMap<>();
  @Getter
  private final ConcurrentHashMap<String, DBInstance> warmUpDBPool = new ConcurrentHashMap<>();


}
