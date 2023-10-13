package com.tmax.serverless.manager;

import com.tmax.serverless.core.container.MainContainer;

public class Main {

  public static void main(String[] args) {
    try {
      System.out.println("Tibero Serverless Manager start!");

      ServerlessManager serverlessManager = (ServerlessManager) MainContainer.getBeanContainer()
          .get(ServerlessManager.class.getName());

      serverlessManager.init();
      serverlessManager.run();
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }
}
