package com.tmax.serverless.core.message.admin;

import static com.tmax.serverless.core.message.AdminMsgType.ADMIN_MSG_DOWN_DB_REPLY;

import com.tmax.serverless.core.annotation.ServerlessMessage;
import com.tmax.serverless.core.message.RegularMessage;
import com.tmax.serverless.core.message.ReturnCode;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@ServerlessMessage(ADMIN_MSG_DOWN_DB_REPLY)
public class AdminMsgDownDBReply extends AdminMsgReply {

  public AdminMsgDownDBReply(RegularMessage header) { super(header); }

  @Builder
  public AdminMsgDownDBReply(ReturnCode returnCode) {
    super(ADMIN_MSG_DOWN_DB_REPLY, returnCode);
  }
}
