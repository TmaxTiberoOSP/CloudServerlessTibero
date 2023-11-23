package com.tmax.serverless.core.message.admin;

import static com.tmax.serverless.core.message.AdminMsgType.ADMIN_MSG_ADD_DB_REPLY;

import com.tmax.serverless.core.annotation.ServerlessMessage;
import com.tmax.serverless.core.message.RegularMessage;
import com.tmax.serverless.core.message.ReturnCode;
import lombok.Builder;
import lombok.ToString;

@ToString(callSuper = true)
@ServerlessMessage(ADMIN_MSG_ADD_DB_REPLY)
public class AdminMsgAddDBReply extends AdminMsgReply {

  public AdminMsgAddDBReply(RegularMessage header) { super(header); }

  @Builder
  public AdminMsgAddDBReply(ReturnCode returnCode) {
    super(ADMIN_MSG_ADD_DB_REPLY, returnCode);
  }
}
