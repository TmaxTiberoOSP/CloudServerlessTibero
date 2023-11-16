package com.tmax.serverless.core.message.admin;

import static com.tmax.serverless.core.message.AdminMsgType.ADMIN_MSG_ADD_DB;
import static com.tmax.serverless.core.message.AdminMsgType.ADMIN_MSG_DB_BOOT;

import com.tmax.serverless.core.annotation.ServerlessMessage;
import com.tmax.serverless.core.context.DBServerlessMode;
import com.tmax.serverless.core.message.JsonMessage;
import com.tmax.serverless.core.message.RegularMessage;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Getter
@ToString
@ServerlessMessage(ADMIN_MSG_ADD_DB)
public class AdminMsgAddDB extends JsonMessage {

  private String dbName;
  private String alias;
  private String ip;
  private int port;
  private String dbUser;
  private String dbPassword;
  private DBServerlessMode mode;

  public AdminMsgAddDB(RegularMessage header) { super(header); }

  @Builder
  public AdminMsgAddDB(String dbName, String alias, String ip, int port,
      String dbUser, String dbPassword, DBServerlessMode mode) {
    super(ADMIN_MSG_ADD_DB);

    this.dbName = dbName;
    this.alias = alias;
    this.ip = ip;
    this.port = port;
    this.dbUser = dbUser;
    this.dbPassword = dbPassword;
    this.mode = mode;
  }

}
