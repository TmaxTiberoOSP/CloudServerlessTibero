package com.tmax.serverless.core.config;

public class ServerlessConst {
  public static final String SM_HOME;
  public static final String SM_IP;
  public static final Integer SM_PORT;
  public static final String SYS_MASTER_IP;
  public static final Integer SYS_MASTER_PORT;
  public static final String SYS_MASTER_URL;
  public static final String SYS_MASTER_DB_TYPE = "TIBERO";
  public static final String SYS_MASTER_DB_COLOR = "#2979FF";
  public static final String PACKAGE_GROUP_NAME = "com.tmax.serverless";

  static {
    SM_HOME = System.getenv("SM_HOME");
    if (SM_HOME == null) {
      throw new RuntimeException("SM_HOME is not set");
    }
    SM_IP = System.getenv("SM_IP");
    if (SM_IP == null) {
      throw new RuntimeException("SM_IP is not set");
    }
    SM_PORT = Integer.valueOf(System.getenv("SM_PORT"));
    if (SM_PORT == null) {
      throw new RuntimeException("SM_PORT is not set");
    }
    SYS_MASTER_IP = System.getenv("SYS_MASTER_IP");
    if (SYS_MASTER_IP == null) {
      throw new RuntimeException("SYS_MASTER_IP is not set");
    }
    SYS_MASTER_PORT = Integer.valueOf(System.getenv("SYS_MASTER_PORT"));
    if (SYS_MASTER_PORT == null) {
      throw new RuntimeException("SYS_MASTER_PORT is not set");
    }
    // url: http://${serverless.sysmaster.host}:${serverless.sysmaster.port}/api/resources
    SYS_MASTER_URL = "http://" + SYS_MASTER_IP + ":" + SYS_MASTER_PORT + "/api";
  }
}
