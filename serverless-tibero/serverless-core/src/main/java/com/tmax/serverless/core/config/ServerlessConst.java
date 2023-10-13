package com.tmax.serverless.core.config;

public class ServerlessConst {
  public static final String TB_HOME;
  public static final String PACKAGE_GROUP_NAME = "com.tmax.serverless";

  static {
    TB_HOME = System.getenv("TB_HOME");

    if (TB_HOME == null) {
      throw new RuntimeException("TB_HOME is not set");
    }
  }
}
