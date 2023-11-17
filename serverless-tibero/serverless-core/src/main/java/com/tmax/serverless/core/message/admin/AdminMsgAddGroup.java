package com.tmax.serverless.core.message.admin;

import static com.tmax.serverless.core.message.AdminMsgType.ADMIN_MSG_ADD_DB;
import static com.tmax.serverless.core.message.AdminMsgType.ADMIN_MSG_ADD_DB_REPLY;
import static com.tmax.serverless.core.message.AdminMsgType.ADMIN_MSG_ADD_GROUP;

import com.tmax.serverless.core.annotation.ServerlessMessage;
import com.tmax.serverless.core.context.DBServerlessMode;
import com.tmax.serverless.core.message.JsonMessage;
import com.tmax.serverless.core.message.RegularMessage;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@ServerlessMessage(ADMIN_MSG_ADD_GROUP)
public class AdminMsgAddGroup extends JsonMessage {

  private String groupName;
  //private List<String> aliasList;

  public AdminMsgAddGroup(RegularMessage header) { super(header); }

  @Builder
  public AdminMsgAddGroup(String groupName /*, List<String> aliasList*/) {
    super(ADMIN_MSG_ADD_GROUP);

    this.groupName = groupName;
    //this.aliasList = aliasList;
  }
}
