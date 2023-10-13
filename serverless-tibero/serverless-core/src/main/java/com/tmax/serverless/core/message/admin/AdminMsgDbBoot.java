package com.tmax.serverless.core.message.admin;

import static com.tmax.serverless.core.message.AdminMsgType.ADMIN_MSG_DB_BOOT;

import com.tmax.serverless.core.annotation.ServerlessMessage;
import com.tmax.serverless.core.message.JsonMessage;
import com.tmax.serverless.core.message.RegularMessage;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@ServerlessMessage(ADMIN_MSG_DB_BOOT)
public class AdminMsgDbBoot extends JsonMessage {

  private String nodeName;

  public AdminMsgDbBoot(RegularMessage header) { super(header); }

  @Builder
  public AdminMsgDbBoot(String nodeName) {
    super(ADMIN_MSG_DB_BOOT);

    this.nodeName = nodeName;
  }
}
