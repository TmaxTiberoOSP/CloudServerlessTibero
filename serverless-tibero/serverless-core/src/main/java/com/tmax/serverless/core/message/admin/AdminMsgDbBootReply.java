package com.tmax.serverless.core.message.admin;

import static com.tmax.serverless.core.message.AdminMsgType.ADMIN_MSG_DB_BOOT_REPLY;

import com.tmax.serverless.core.annotation.ServerlessMessage;
import com.tmax.serverless.core.message.JsonMessage;
import com.tmax.serverless.core.message.RegularMessage;
import com.tmax.serverless.core.message.ReturnCode;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@ServerlessMessage(ADMIN_MSG_DB_BOOT_REPLY)
public class AdminMsgDbBootReply extends JsonMessage {
  private ReturnCode returnCode;

  public AdminMsgDbBootReply(RegularMessage header) { super(header); }

  @Builder
  public AdminMsgDbBootReply(ReturnCode returnCode) {
    super(ADMIN_MSG_DB_BOOT_REPLY);

    this.returnCode = returnCode;
  }

}
