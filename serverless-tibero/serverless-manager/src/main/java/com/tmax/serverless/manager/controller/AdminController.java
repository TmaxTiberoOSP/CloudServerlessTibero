package com.tmax.serverless.manager.controller;

import static com.tmax.serverless.core.message.ReturnCode.FAIL;
import static com.tmax.serverless.core.message.ReturnCode.SUCCESS;

import com.tmax.serverless.core.annotation.Autowired;
import com.tmax.serverless.core.annotation.Controller;
import com.tmax.serverless.core.annotation.ServerlessMessageMapping;
import com.tmax.serverless.core.message.admin.AdminMsgAddDB;
import com.tmax.serverless.core.message.admin.AdminMsgAddDBReply;
import com.tmax.serverless.core.message.admin.AdminMsgAddGroup;
import com.tmax.serverless.core.message.admin.AdminMsgAddGroupReply;
import com.tmax.serverless.core.message.admin.AdminMsgBootDB;
import com.tmax.serverless.core.message.admin.AdminMsgBootDBReply;
import com.tmax.serverless.core.message.admin.AdminMsgDownDB;
import com.tmax.serverless.core.message.admin.AdminMsgDownDBReply;
import com.tmax.serverless.core.message.admin.AdminMsgScaleIn;
import com.tmax.serverless.core.message.admin.AdminMsgScaleInReply;
import com.tmax.serverless.core.message.admin.AdminMsgScaleOut;
import com.tmax.serverless.core.message.admin.AdminMsgScaleOutComplete;
import com.tmax.serverless.core.message.admin.AdminMsgScaleOutCompleteReply;
import com.tmax.serverless.core.message.admin.AdminMsgScaleOutReply;
import com.tmax.serverless.manager.service.PoolManagementService;
import io.netty.channel.ChannelHandlerContext;
import java.io.IOException;
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

  @ServerlessMessageMapping(AdminMsgAddGroup.class)
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

  @ServerlessMessageMapping(AdminMsgScaleIn.class)
  public AdminMsgScaleInReply scaleInDBInstance(
      ChannelHandlerContext ctx, AdminMsgScaleIn req) {
    log.info("{}", req);

    boolean result = false;
    try {
      result = poolManagementService.scaleInDB(req.getAlias());
    } catch (IOException e) {
      log.info("ScaleInDB Fail. IOException.");
    }
    AdminMsgScaleInReply res = AdminMsgScaleInReply.builder()
        .returnCode(result ? SUCCESS : FAIL)
        .build();

    log.info("{}", res);
    return res;
  }

  @ServerlessMessageMapping(AdminMsgScaleOut.class)
  public AdminMsgScaleOutReply scaleOutDBInstance(
      ChannelHandlerContext ctx, AdminMsgScaleOut req) {
    log.info("{}", req);

    boolean result = false;
    try {
      result = poolManagementService.scaleOutDB(req.getAlias());
    } catch (IOException e) {
      log.info("ScaleOutDB Fail. IOException.");
    }
    AdminMsgScaleOutReply res = AdminMsgScaleOutReply.builder()
        .returnCode(result ? SUCCESS : FAIL)
        .build();

    log.info("{}", res);
    return res;
  }

  @ServerlessMessageMapping(AdminMsgScaleOutComplete.class)
  public AdminMsgScaleOutCompleteReply scaleOutCompleteDBInstance(
      ChannelHandlerContext ctx, AdminMsgScaleOutComplete req) {
    log.info("{}", req);

    boolean result = false;
    try {
      result = poolManagementService.makeDBWarmUp(req.getAlias());
    } catch (IOException e) {
      log.info("ScaleOutCompleteDB Fail. IOException.");
    }
    AdminMsgScaleOutCompleteReply res = AdminMsgScaleOutCompleteReply.builder()
        .returnCode(result ? SUCCESS : FAIL)
        .build();

    log.info("{}", res);
    return res;
  }

//  @ServerlessMessageMapping(AdminMsgBootDB.class)
//  public AdminMsgBootDBReply bootDBInstance(
//      ChannelHandlerContext ctx, AdminMsgBootDB req) {
//    log.info("{}", req);
//
//    boolean result;
//
//    AdminMsgBootDBReply res = AdminMsgBootDBReply.builder()
//        .returnCode(result ? SUCCESS : FAIL)
//        .build();
//
//    log.info("{}", res);
//    return res;
//  }
//
//  @ServerlessMessageMapping(AdminMsgDownDB.class)
//  public AdminMsgDownDBReply downDBInstance(
//      ChannelHandlerContext ctx, AdminMsgDownDB req) {
//    log.info("{}", req);
//
//    boolean result;
//
//    AdminMsgDownDBReply res = AdminMsgDownDBReply.builder()
//        .returnCode(result ? SUCCESS : FAIL)
//        .build();
//
//    log.info("{}", res);
//    return res;
//  }

}
