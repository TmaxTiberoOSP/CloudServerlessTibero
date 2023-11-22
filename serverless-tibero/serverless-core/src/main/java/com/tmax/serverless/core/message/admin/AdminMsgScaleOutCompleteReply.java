package com.tmax.serverless.core.message.admin;

import static com.tmax.serverless.core.message.AdminMsgType.ADMIN_MSG_SCALE_OUT_COMPLETE;
import static com.tmax.serverless.core.message.AdminMsgType.ADMIN_MSG_SCALE_OUT_COMPLETE_REPLY;
import static com.tmax.serverless.core.message.AdminMsgType.ADMIN_MSG_SCALE_OUT_REPLY;

import com.tmax.serverless.core.annotation.ServerlessMessage;
import com.tmax.serverless.core.message.RegularMessage;
import com.tmax.serverless.core.message.ReturnCode;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@ToString(callSuper = true)
@ServerlessMessage(ADMIN_MSG_SCALE_OUT_COMPLETE_REPLY)
public class AdminMsgScaleOutCompleteReply extends AdminMsgReply {
  public AdminMsgScaleOutCompleteReply(RegularMessage header) { super(header); }

  @Builder
  public AdminMsgScaleOutCompleteReply(ReturnCode returnCode) {
    super(ADMIN_MSG_SCALE_OUT_COMPLETE_REPLY, returnCode);
  }
}
