package com.tmax.serverless.core.handler.codec;

import com.tmax.serverless.core.container.MainContainer;
import com.tmax.serverless.core.message.JsonMessage;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class JsonMessageEncoder extends MessageToByteEncoder<JsonMessage> {

  @Override
  protected void encode(ChannelHandlerContext ctx, JsonMessage msg, ByteBuf out) {
    if (log.isDebugEnabled()) {
      log.debug("{}", msg);
    }

    String json = MainContainer.getGson().toJson(msg);

    out.writeIntLE(msg.getMsgMagicNumber());
    out.writeIntLE(msg.getMsgFlag());
    out.writeIntLE(msg.getMsgType());
    out.writeIntLE(json.length());
    out.writeBytes(json.getBytes());
  }
}
