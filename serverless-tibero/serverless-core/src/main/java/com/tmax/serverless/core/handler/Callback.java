package com.tmax.serverless.core.handler;

import com.tmax.serverless.core.message.JsonMessage;
import io.netty.channel.ChannelHandlerContext;

@FunctionalInterface
public interface Callback<T> {

  void run(ChannelHandlerContext ctx, JsonMessage response);
}
