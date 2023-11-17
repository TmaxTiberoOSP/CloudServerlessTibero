package com.tmax.serverless.manager.controller;

import static com.tmax.serverless.core.message.ReturnCode.FAIL;
import static com.tmax.serverless.core.message.ReturnCode.SUCCESS;

import com.tmax.serverless.core.annotation.Autowired;
import com.tmax.serverless.core.annotation.Controller;
import com.tmax.serverless.core.annotation.ServerlessMessageMapping;
import com.tmax.serverless.core.message.ReturnCode;
import com.tmax.serverless.core.message.admin.AdminMsgAddDB;
import com.tmax.serverless.core.message.admin.AdminMsgAddDBReply;
import com.tmax.serverless.core.message.admin.AdminMsgAddGroup;
import com.tmax.serverless.core.message.admin.AdminMsgAddGroupReply;
import com.tmax.serverless.core.message.admin.AdminMsgDbBoot;
import com.tmax.serverless.core.message.admin.AdminMsgDbBootReply;
import com.tmax.serverless.manager.service.PoolManagementService;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
public class AdminController {
  @Autowired
  PoolManagementService poolManagementService;

  @ServerlessMessageMapping(AdminMsgAddDB.class)
  public AdminMsgAddDBReply addDBInstance(ChannelHandlerContext ctx, AdminMsgAddDB req) {
    log.info("{}", req);

    boolean result = poolManagementService.addDBtoInstancePool(req);
    AdminMsgAddDBReply res = AdminMsgAddDBReply.builder()
            .returnCode(result ? SUCCESS : FAIL)
            .build();

    log.info("{}", res);

    return res;
  }

  public AdminMsgAddGroupReply addGroupForMonitoring(
      ChannelHandlerContext ctx, AdminMsgAddGroup req) {
    log.info("{}", req);

    boolean result = poolManagementService.addGroupForMonitoring(req);
    AdminMsgAddGroupReply res = AdminMsgAddGroupReply.builder()
        .returnCode(result ? SUCCESS : FAIL)
        .build();

    log.info("{}", res);

    return res;
  }
}
