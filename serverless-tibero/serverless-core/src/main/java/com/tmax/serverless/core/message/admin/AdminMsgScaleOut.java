package com.tmax.serverless.core.message.admin;

import static com.tmax.serverless.core.message.AdminMsgType.ADMIN_MSG_ADD_GROUP;
import static com.tmax.serverless.core.message.AdminMsgType.ADMIN_MSG_SCALE_IN;
import static com.tmax.serverless.core.message.AdminMsgType.ADMIN_MSG_SCALE_OUT;

import com.tmax.serverless.core.annotation.ServerlessMessage;
import com.tmax.serverless.core.message.JsonMessage;
import com.tmax.serverless.core.message.RegularMessage;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@ServerlessMessage(ADMIN_MSG_SCALE_OUT)
public class AdminMsgScaleOut extends JsonMessage {
  private String dbName;
  private String alias;
  private String ip;
  private int port;

  public AdminMsgScaleOut(RegularMessage header) { super(header); }

  @Builder
  public AdminMsgScaleOut(String dbName, String alias, String ip, int port) {
    super(ADMIN_MSG_SCALE_OUT);

    this.dbName = dbName;
    this.alias = alias;
    this.ip = ip;
    this.port = port;
  }
}
