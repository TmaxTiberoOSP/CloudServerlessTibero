package com.tmax.serverless.core.message.admin;

import static com.tmax.serverless.core.message.AdminMsgType.ADMIN_MSG_BOOT_DB;

import com.tmax.serverless.core.annotation.ServerlessMessage;
import com.tmax.serverless.core.message.JsonMessage;
import com.tmax.serverless.core.message.RegularMessage;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@ServerlessMessage(ADMIN_MSG_BOOT_DB)
public class AdminMsgBootDB extends JsonMessage {

  private String dbName;
  private String alias;
  private String ip;
  private int port;


  public AdminMsgBootDB(RegularMessage header) { super(header); }

  @Builder
  public AdminMsgBootDB(String dbName, String alias, String ip, int port) {
    super(ADMIN_MSG_BOOT_DB);

    this.dbName = dbName;
    this.alias = alias;
    this.ip = ip;
    this.port = port;
  }
}
