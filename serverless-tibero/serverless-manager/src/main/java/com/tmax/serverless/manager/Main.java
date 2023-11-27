package com.tmax.serverless.manager;

import com.tmax.serverless.core.container.MainContainer;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Main {

  public static void main(String[] args) {
    try {
      System.out.println("Tibero Serverless Manager start!");
      log.info("Tibero Serverless Manager start!");

      ServerlessManager serverlessManager = (ServerlessManager) MainContainer.getBeanContainer()
          .get(ServerlessManager.class.getName());

      serverlessManager.init();
      serverlessManager.run();
      log.info("Tibero Serverless Manager Shutdown!");
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }
}
