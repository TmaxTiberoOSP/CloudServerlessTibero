package com.tmax.serverless.manager.context;

import java.util.concurrent.ConcurrentHashMap;

public class DBInstancePool {
  private final ConcurrentHashMap<String, DBInstance> activeDBPool = new ConcurrentHashMap<>();
  private final ConcurrentHashMap<String, DBInstance> activeColdDBPool = new ConcurrentHashMap<>();
  private final ConcurrentHashMap<String, DBInstance> warmUpDBPool = new ConcurrentHashMap<>();

}
