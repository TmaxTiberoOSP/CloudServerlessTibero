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
    log.info("encode1: " + msg);
    log.info("encode1-1: " + MainContainer.getGson());
    String json = MainContainer.getGson().toJson(msg);
    log.info("encode2: " + json);
    out.writeIntLE(msg.getMsgMagicNumber());
    out.writeIntLE(msg.getMsgFlag());
    out.writeIntLE(msg.getMsgType());
    out.writeIntLE(json.length());
    out.writeBytes(json.getBytes());
    log.info("encode3");
  }
}
