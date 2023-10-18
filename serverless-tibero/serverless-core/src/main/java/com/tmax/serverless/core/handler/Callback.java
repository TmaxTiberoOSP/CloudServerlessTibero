package com.tmax.serverless.core.handler;

import com.tmax.serverless.core.message.JsonMessage;
import com.tmax.serverless.core.message.admin.AdminMsgReply;
import io.netty.channel.ChannelHandlerContext;

@FunctionalInterface
public interface Callback<T> {

  void run(ChannelHandlerContext ctx, AdminMsgReply response);
}
