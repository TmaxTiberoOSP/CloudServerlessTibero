package com.tmax.serverless.manager.controller;

import com.tmax.serverless.core.annotation.Controller;
import com.tmax.serverless.core.annotation.ServerlessMessageMapping;
import com.tmax.serverless.core.message.admin.AdminMsgDbBoot;
import com.tmax.serverless.core.message.admin.AdminMsgDbBootReply;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
public class AdminController {

  @ServerlessMessageMapping(AdminMsgDbBoot.class)
  public AdminMsgDbBootReply greetingAgent(ChannelHandlerContext ctx, AdminMsgDbBootReply request) {
    log.info("{}", request);



    AdminMsgDbBootReply response = AdminMsgDbBootReply.builder().build();
    log.info("{}", response);

    return response;
  }
}
