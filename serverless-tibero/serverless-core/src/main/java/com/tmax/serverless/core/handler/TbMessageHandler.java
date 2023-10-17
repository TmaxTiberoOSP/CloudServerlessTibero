package com.tmax.serverless.core.handler;

import com.tmax.serverless.core.container.BeanContainer;
import com.tmax.serverless.core.container.MainContainer;
import com.tmax.serverless.core.container.ServiceContainer;
import com.tmax.serverless.core.message.EmptyMessage;
import com.tmax.serverless.core.message.RegularMessage;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import java.lang.reflect.Method;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TbMessageHandler extends ChannelInboundHandlerAdapter {

  @Override
  public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
    ServiceContainer serviceContainer = MainContainer.getServiceContainer();
    BeanContainer beanContainer = MainContainer.getBeanContainer();
    RegularMessage req = (RegularMessage) msg;
    log.info("TbMessageHadnler1");
    if (log.isDebugEnabled()) {
      log.debug("| Manager | <- | Admin |: {}", req);
    }

    Method service = serviceContainer.getServerlessService(req.getMsgType());
    if (service == null) {
      log.error("Mapped service does not exist: {}", req.getMsgType());
      return;
    }
    log.info("TbMessageHadnler2");
    Object controller = beanContainer.get(service.getDeclaringClass().getName());
    if (controller == null) {
      log.error("Mapped controller does not exist: {}", req.getMsgType());
      return;
    }
    log.info("TbMessageHadnler3");
    Object res = service.invoke(controller, ctx, req);
    if (res != null) {
      ctx.writeAndFlush(res);
    } else {
      /* CallbackHandler write 함수가 호출 될 수 있도록 빈 메세지 전송처리 */
      ctx.writeAndFlush(new EmptyMessage());
    }
  }

  @Override
  public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
    log.error("", cause);
    ctx.close();
  }
}
