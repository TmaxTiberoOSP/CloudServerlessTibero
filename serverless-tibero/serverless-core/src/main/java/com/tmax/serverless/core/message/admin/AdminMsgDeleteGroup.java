package com.tmax.serverless.core.message.admin;

import static com.tmax.serverless.core.message.AdminMsgType.ADMIN_MSG_DELETE_GROUP;

import com.tmax.serverless.core.annotation.ServerlessMessage;
import com.tmax.serverless.core.message.JsonMessage;
import com.tmax.serverless.core.message.RegularMessage;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@ServerlessMessage(ADMIN_MSG_DELETE_GROUP)
public class AdminMsgDeleteGroup extends JsonMessage {
  private String groupName;

  public AdminMsgDeleteGroup(RegularMessage header) { super(header); }

  @Builder
  public AdminMsgDeleteGroup(String groupName) {
    super(ADMIN_MSG_DELETE_GROUP);

    this.groupName = groupName;
  }
}
