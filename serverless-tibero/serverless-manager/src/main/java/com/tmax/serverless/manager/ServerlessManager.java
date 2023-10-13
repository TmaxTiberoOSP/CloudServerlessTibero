package com.tmax.serverless.manager;

import com.tmax.serverless.core.annotation.Component;
import com.tmax.serverless.core.annotation.Value;
import com.tmax.serverless.core.handler.codec.JsonMessageEncoder;
import com.tmax.serverless.core.handler.codec.TbMessageDecoder;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

@Component
public class ServerlessManager {

  private final EventLoopGroup listenerGroup = new NioEventLoopGroup();
  private final EventLoopGroup workerGroup = new NioEventLoopGroup();
  @Value("${serverless.manager.host}")
  private String host;
  @Value("${serverless.manager.port}")
  private int port;

  public void init() {

  }

  public void run() throws InterruptedException {
    ServerBootstrap serverBootstrap = new ServerBootstrap()
        .group(listenerGroup, workerGroup)
        // TODO: EpollServerSocketChannel(Linux only) 사용 논의 필요
        .channel(NioServerSocketChannel.class)
        .option(ChannelOption.SO_BACKLOG, 128)
        .childOption(ChannelOption.SO_KEEPALIVE, true)
        .childOption(ChannelOption.SO_REUSEADDR, true)
        .childHandler((new ChannelInitializer<SocketChannel>() {
          @Override
          protected void initChannel(SocketChannel ch) {
            ch.pipeline()
                .addLast(
                    new TbMessageDecoder(),
                    new JsonMessageEncoder()
                );

          }
        }));

    serverBootstrap.bind(host, port).sync();
  }
}
