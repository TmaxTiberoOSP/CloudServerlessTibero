package com.tmax.serverless.core.message.admin;

import static com.tmax.serverless.core.message.AdminMsgType.ADMIN_MSG_ADD_GROUP;
import static com.tmax.serverless.core.message.AdminMsgType.ADMIN_MSG_SCALE_IN;

import com.tmax.serverless.core.annotation.ServerlessMessage;
import com.tmax.serverless.core.message.JsonMessage;
import com.tmax.serverless.core.message.RegularMessage;
import java.util.List;
import lombok.Builder;
import lombok.ToString;

@ToString(callSuper = true)
@ServerlessMessage(ADMIN_MSG_SCALE_IN)
public class AdminMsgScaleIn extends JsonMessage {
  private String dbName;
  private String alias;
  private String ip;
  private int port;

  public AdminMsgScaleIn(RegularMessage header) { super(header); }

  @Builder
  public AdminMsgScaleIn(String dbName, String alias, String ip, int port) {
    super(ADMIN_MSG_SCALE_IN);

    this.dbName = dbName;
    this.alias = alias;
    this.ip = ip;
    this.port = port;
  }
}
