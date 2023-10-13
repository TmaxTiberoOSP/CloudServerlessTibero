package com.tmax.serverless.core.message.admin;

import static com.tmax.serverless.core.message.AdminMsgType.ADMIN_MSG_ADD_DB;
import static com.tmax.serverless.core.message.AdminMsgType.ADMIN_MSG_ADD_DB_REPLY;

import com.tmax.serverless.core.annotation.ServerlessMessage;
import com.tmax.serverless.core.context.DBServerlessMode;
import com.tmax.serverless.core.message.JsonMessage;
import com.tmax.serverless.core.message.RegularMessage;
import lombok.Builder;

@ServerlessMessage(ADMIN_MSG_ADD_DB_REPLY)
public class AdminMsgAddDBReply extends JsonMessage {

  public AdminMsgAddDBReply(RegularMessage header) { super(header); }

  @Builder
  public AdminMsgAddDBReply() {
    super(ADMIN_MSG_ADD_DB);

  }
}
