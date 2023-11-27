package com.tmax.serverless.core.message.admin;

import static com.tmax.serverless.core.message.AdminMsgType.ADMIN_MSG_DOWN_DB_REPLY;
import static com.tmax.serverless.core.message.AdminMsgType.ADMIN_MSG_DOWN_MANAGER_REPLY;

import com.tmax.serverless.core.annotation.ServerlessMessage;
import com.tmax.serverless.core.message.RegularMessage;
import com.tmax.serverless.core.message.ReturnCode;
import lombok.Builder;
import lombok.ToString;

@ToString(callSuper = true)
@ServerlessMessage(ADMIN_MSG_DOWN_MANAGER_REPLY)
public class AdminMsgDownManagerReply extends AdminMsgReply {
  public AdminMsgDownManagerReply(RegularMessage header) { super(header); }

  @Builder
  public AdminMsgDownManagerReply(ReturnCode returnCode) {
    super(ADMIN_MSG_DOWN_MANAGER_REPLY, returnCode);
  }

}
