package com.tmax.serverless.core.message.admin;

import static com.tmax.serverless.core.message.AdminMsgType.ADMIN_MSG_BOOT_DB_REPLY;

import com.tmax.serverless.core.annotation.ServerlessMessage;
import com.tmax.serverless.core.message.RegularMessage;
import com.tmax.serverless.core.message.ReturnCode;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@ToString(callSuper = true)
@ServerlessMessage(ADMIN_MSG_BOOT_DB_REPLY)
public class AdminMsgBootDBReply extends AdminMsgReply {

  public AdminMsgBootDBReply(RegularMessage header) { super(header); }

  @Builder
  public AdminMsgBootDBReply(ReturnCode returnCode) {
    super(ADMIN_MSG_BOOT_DB_REPLY, returnCode);
  }
}
