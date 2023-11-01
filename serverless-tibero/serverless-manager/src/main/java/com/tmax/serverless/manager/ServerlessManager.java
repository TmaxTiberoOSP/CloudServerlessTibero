package com.tmax.serverless.manager;

import com.tmax.serverless.core.Client;
import com.tmax.serverless.core.annotation.Component;
import com.tmax.serverless.core.annotation.Value;
import com.tmax.serverless.core.handler.TbMessageHandler;
import com.tmax.serverless.core.handler.WebSocketClientHandler;
import com.tmax.serverless.core.handler.codec.JsonMessageEncoder;
import com.tmax.serverless.core.handler.codec.TbMessageDecoder;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import io.netty.handler.codec.http.DefaultHttpHeaders;
import io.netty.handler.codec.http.websocketx.WebSocketClientHandshakerFactory;
import io.netty.handler.codec.http.websocketx.WebSocketVersion;
import java.net.URI;
import java.net.URISyntaxException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class ServerlessManager {

  private final EventLoopGroup listenerGroup = new NioEventLoopGroup();
  private final EventLoopGroup workerGroup = new NioEventLoopGroup();
  @Value("${serverless.manager.host}")
  private String host;
  @Value("${serverless.manager.port}")
  private int port;
  @Value("${serverless.sysmaster.host}")
  private String sysMasterHost;
  @Value("${serverless.sysmaster.port}")
  private int sysMasterPort;
  private Client client;

  public void init() {

  }

  public void run() throws InterruptedException, URISyntaxException {
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
                    new JsonMessageEncoder())
                .addLast(TbMessageHandler.class.getName(), new TbMessageHandler());
          }
        }));

    serverBootstrap.bind(host, port).sync();
    log.info("Serverless Manager Netty Server configuration complete.");

    runClient();
  }

  public void runClient() throws URISyntaxException {
    client = Client.builder()
        .host(sysMasterHost)
        .port(sysMasterPort)
        .sysMasterBuild();

    log.info("{}", client);

    boolean connected = client.connect();
    if (connected) {
      try {
        client.getWebSocketHandler().handshakeFuture().sync();
      } catch (InterruptedException e) {
        throw new RuntimeException(e);
      }
    }

  }


}
