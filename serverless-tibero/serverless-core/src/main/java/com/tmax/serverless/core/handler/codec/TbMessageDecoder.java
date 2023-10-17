package com.tmax.serverless.core.handler.codec;

import com.tmax.serverless.core.container.MainContainer;
import com.tmax.serverless.core.message.JsonMessage;
import com.tmax.serverless.core.message.RegularMessage;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import java.lang.reflect.Constructor;
import java.util.List;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TbMessageDecoder extends ByteToMessageDecoder {

  @Override
  protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
    log.info("decode1");
    int readableBytes = in.readableBytes();
    if (log.isDebugEnabled()) {
      log.debug("readableBytes: {}", readableBytes);
    }

    /* 메세지 헤더 크기보다 데이터가 적게 온 경우 */
    if (readableBytes < RegularMessage.SIZE) {
      return;
    }

    RegularMessage regular = new RegularMessage(in);
    /* 메세지 바디 크기보다 데이터가 적게 온 경우 */
    if (readableBytes < regular.getTotalLength()) {
      in.resetReaderIndex();
      return;
    }

    regular.setBytes(in);

    Constructor<?> messageConstructor = MainContainer.getServiceContainer()
        .getMessageConstructor(regular.getMsgType());
    log.info("decode2: " + messageConstructor);
    if (messageConstructor == null) {
      log.error("uninterpretable message");
      return;
    }

    Object msg = messageConstructor.newInstance(regular);
    log.info("decode3: " + msg);
    if (msg instanceof JsonMessage) {
      ((JsonMessage) msg).deserialize(msg.getClass());
    } else {
      throw new RuntimeException("Invalid Message Type!");
    }
    log.info("decode4: " + msg);
    out.add(msg);

    if (log.isDebugEnabled()) {
      log.debug("{}", msg);
    }
  }
}
