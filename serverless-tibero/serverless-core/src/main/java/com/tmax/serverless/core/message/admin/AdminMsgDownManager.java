package com.tmax.serverless.core.message.admin;

import static com.tmax.serverless.core.message.AdminMsgType.ADMIN_MSG_DOWN_MANAGER;

import com.tmax.serverless.core.annotation.ServerlessMessage;
import com.tmax.serverless.core.message.JsonMessage;
import com.tmax.serverless.core.message.RegularMessage;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@ServerlessMessage(ADMIN_MSG_DOWN_MANAGER)
public class AdminMsgDownManager extends JsonMessage {

  public AdminMsgDownManager(RegularMessage header) { super(header); }

  @Builder
  public AdminMsgDownManager() {
    super(ADMIN_MSG_DOWN_MANAGER);
  }
}
