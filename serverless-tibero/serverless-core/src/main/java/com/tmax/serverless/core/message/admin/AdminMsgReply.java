package com.tmax.serverless.core.message.admin;

import static com.tmax.serverless.core.message.ReturnCode.SUCCESS;

import com.tmax.serverless.core.message.JsonMessage;
import com.tmax.serverless.core.message.RegularMessage;
import com.tmax.serverless.core.message.ReturnCode;
import lombok.Getter;
import lombok.ToString;

@ToString
@Getter
public class AdminMsgReply extends JsonMessage {
  private ReturnCode result;

  public AdminMsgReply(RegularMessage header) {
    super(header);
  }
  public AdminMsgReply(int msgType, ReturnCode result) {
    super(msgType);
    this.result = result;
  }

  public boolean isSuccess() {
    return result == SUCCESS;
  }
}
