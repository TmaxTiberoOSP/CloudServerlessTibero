package com.tmax.serverless.core.message.admin;

import static com.tmax.serverless.core.message.AdminMsgType.ADMIN_MSG_ADD_GROUP_REPLY;

import com.tmax.serverless.core.annotation.ServerlessMessage;
import com.tmax.serverless.core.message.RegularMessage;
import com.tmax.serverless.core.message.ReturnCode;
import lombok.Builder;
import lombok.ToString;

@ToString(callSuper = true)
@ServerlessMessage(ADMIN_MSG_ADD_GROUP_REPLY)
public class AdminMsgAddGroupReply extends AdminMsgReply {

  public AdminMsgAddGroupReply(RegularMessage header) { super(header); }

  @Builder
  public AdminMsgAddGroupReply(ReturnCode returnCode) {
    super(ADMIN_MSG_ADD_GROUP_REPLY, returnCode);
  }
}
