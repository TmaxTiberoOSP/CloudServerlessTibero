package com.tmax.serverless.core.message.admin;

import static com.tmax.serverless.core.message.AdminMsgType.ADMIN_MSG_SCALE_IN_COMPLETE_REPLY;

import com.tmax.serverless.core.annotation.ServerlessMessage;
import com.tmax.serverless.core.message.RegularMessage;
import com.tmax.serverless.core.message.ReturnCode;
import lombok.Builder;
import lombok.ToString;

@ToString(callSuper = true)
@ServerlessMessage(ADMIN_MSG_SCALE_IN_COMPLETE_REPLY)
public class AdminMsgScaleInCompleteReply extends AdminMsgReply {
  public AdminMsgScaleInCompleteReply(RegularMessage header) { super(header); }

  @Builder
  public AdminMsgScaleInCompleteReply(ReturnCode returnCode) {
    super(ADMIN_MSG_SCALE_IN_COMPLETE_REPLY, returnCode);
  }
}
