package com.tmax.serverless.core.message.admin;

import static com.tmax.serverless.core.message.AdminMsgType.ADMIN_MSG_DELETE_DB;

import com.tmax.serverless.core.annotation.ServerlessMessage;
import com.tmax.serverless.core.message.JsonMessage;
import com.tmax.serverless.core.message.RegularMessage;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@ServerlessMessage(ADMIN_MSG_DELETE_DB)
public class AdminMsgDeleteDB extends JsonMessage {

  private String dbName;
  private String alias;

  public AdminMsgDeleteDB(RegularMessage header) { super(header); }

  @Builder
  public AdminMsgDeleteDB(String dbName, String alias) {
    super(ADMIN_MSG_DELETE_DB);

    this.dbName = dbName;
    this.alias = alias;
  }
}
