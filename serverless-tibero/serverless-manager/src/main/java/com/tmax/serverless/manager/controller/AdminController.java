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
import com.tmax.serverless.core.message.admin.AdminMsgScaleIn;
import com.tmax.serverless.core.message.admin.AdminMsgScaleInReply;
import com.tmax.serverless.core.message.admin.AdminMsgScaleOut;
import com.tmax.serverless.core.message.admin.AdminMsgScaleOutComplete;
import com.tmax.serverless.core.message.admin.AdminMsgScaleOutCompleteReply;
import com.tmax.serverless.core.message.admin.AdminMsgScaleOutReply;
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
    log.info("addDBInstance: {}", req);

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
    log.info("addGroupForMonitoring: {}", req);

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
    log.info("scaleInDBInstance: {}", req);

    boolean result = poolManagementService.scaleInDB(req.getAlias());
    AdminMsgScaleInReply res = AdminMsgScaleInReply.builder()
        .returnCode(result ? SUCCESS : FAIL)
        .build();

    log.info("{}", res);
    return res;
  }

  @ServerlessMessageMapping(AdminMsgScaleOut.class)
  public AdminMsgScaleOutReply scaleOutDBInstance(
      ChannelHandlerContext ctx, AdminMsgScaleOut req) {
    log.info("scaleOutDBInstance: {}", req);

    boolean result = poolManagementService.scaleOutDB(req.getAlias());

    AdminMsgScaleOutReply res = AdminMsgScaleOutReply.builder()
        .returnCode(result ? SUCCESS : FAIL)
        .build();

    log.info("{}", res);
    return res;
  }

  @ServerlessMessageMapping(AdminMsgScaleOutComplete.class)
  public AdminMsgScaleOutCompleteReply scaleOutCompleteDBInstance(
      ChannelHandlerContext ctx, AdminMsgScaleOutComplete req) {
    log.info("scaleOutCompleteDBInstance: {}", req);

    boolean result = poolManagementService.makeDBWarmUp(req.getAlias());

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
