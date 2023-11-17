package com.tmax.serverless.core.message.admin;

import static com.tmax.serverless.core.message.AdminMsgType.ADMIN_MSG_ADD_DB_REPLY;
import static com.tmax.serverless.core.message.AdminMsgType.ADMIN_MSG_ADD_GROUP_REPLY;
import static com.tmax.serverless.core.message.AdminMsgType.ADMIN_MSG_SCALE_IN;
import static com.tmax.serverless.core.message.AdminMsgType.ADMIN_MSG_SCALE_IN_REPLY;

import com.tmax.serverless.core.annotation.ServerlessMessage;
import com.tmax.serverless.core.message.RegularMessage;
import com.tmax.serverless.core.message.ReturnCode;
import lombok.Builder;
import lombok.ToString;

@ToString(callSuper = true)
@ServerlessMessage(ADMIN_MSG_SCALE_IN_REPLY)
public class AdminMsgScaleInReply extends AdminMsgReply {

  public AdminMsgScaleInReply(RegularMessage header) { super(header); }

  @Builder
  public AdminMsgScaleInReply(ReturnCode returnCode) {
    super(ADMIN_MSG_SCALE_IN_REPLY, returnCode);
  }
}
