package com.tmax.serverless.core.handler;

import io.netty.channel.Channel;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import java.lang.reflect.ParameterizedType;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

@Slf4j
public abstract class CallbackHandler<T> extends ChannelDuplexHandler {

  private final String callbackHandlerName;
  private final Class<?> targetMessage;
  private boolean isTarget = false;

  public CallbackHandler(String callbackHandlerName) {
    this.callbackHandlerName = callbackHandlerName;
    this.targetMessage = ((Class<?>) ((ParameterizedType) getClass()
        .getGenericSuperclass()).getActualTypeArguments()[0]);
  }

  private static void add(ChannelPipeline pipe, CallbackHandler<?> callback) {
    pipe.addLast(callback.callbackHandlerName, callback);
  }

  public static void add(Channel ch, CallbackHandler<?> callback) {
    add(ch.pipeline(), callback);
  }

  @Override
  public void channelRead(@NotNull ChannelHandlerContext ctx, @NotNull Object msg)
      throws Exception {
    isTarget = targetMessage.isInstance(msg);

    if (log.isDebugEnabled()) {
      log.debug("{}:{}", isTarget, msg);
    }

    if (isTarget) {
      callback(ctx, msg);
      ctx.channel().pipeline().remove(this);
    }
  }

  public abstract boolean callback(ChannelHandlerContext ctx, Object msg);
}
