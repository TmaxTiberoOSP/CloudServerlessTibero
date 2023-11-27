package com.tmax.serverless.manager.service;

import com.tmax.serverless.core.annotation.Autowired;
import com.tmax.serverless.core.annotation.Service;
import com.tmax.serverless.manager.ServerlessManager;
import io.netty.channel.ChannelFuture;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class SystemService {
  @Autowired
  private ServerlessManager serverlessManager;

  public void shutdownManager() {
    serverlessManager.getPromise().complete(0);
  }

}
