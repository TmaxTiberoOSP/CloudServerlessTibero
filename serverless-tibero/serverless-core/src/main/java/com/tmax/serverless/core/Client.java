package com.tmax.serverless.core;

import com.tmax.serverless.core.handler.ConnectionObservable;
import com.tmax.serverless.core.handler.WebSocketClientHandler;
import com.tmax.serverless.core.handler.codec.JsonMessageEncoder;
import com.tmax.serverless.core.handler.codec.TbMessageDecoder;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.DefaultHttpHeaders;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.websocketx.WebSocketClientHandshakerFactory;
import io.netty.handler.codec.http.websocketx.WebSocketVersion;
import io.netty.handler.codec.http.websocketx.extensions.compression.WebSocketClientCompressionHandler;
import io.netty.util.AttributeKey;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

@Slf4j
@ToString
@Builder(buildMethodName = "init")
public class Client implements PropertyChangeListener {

  private final CountDownLatch cancel = new CountDownLatch(1);
  private String host;
  private int port;
  @Builder.Default
  private boolean persistence = false;
  @Builder.Default
  private long retryMax = 20;
  @Builder.Default
  private long interval = 3000;
  @Builder.Default
  private int nThreads = 1;
  private NioEventLoopGroup eventLoop;
  private Bootstrap bootstrap;

  @Getter
  private ChannelFuture future;

  public boolean connect() {
    final ExecutorService executor = Executors.newSingleThreadExecutor();

    try {
      return executor.submit(() -> {
        if (persistence) {
          for (long retryCnt = 0; retryCnt <= retryMax; retryCnt++) {
            if (tryConnect()) {
              return true;
            } else if (cancel.await(interval, TimeUnit.MILLISECONDS)) {
              log.warn("reconnect stop");
              return false;
            } else if (retryCnt > 0) {
              log.warn("reconnect attempt ({}/{})", retryCnt, retryMax);
            }
          }
          return false;
        } else {
          return tryConnect();
        }
      }).get();
    } catch (InterruptedException | ExecutionException e) {
      log.warn("", e);
      return false;
    }
  }

  private boolean tryConnect() {
    final ChannelFuture future;

    try {
      future = bootstrap.connect().sync();

      if (future.isSuccess()) {
        this.future = future;

        if (persistence) {
          future.channel().pipeline()
              .addFirst(new ConnectionObservable(this));
        }

        return true;
      }
    } catch (Exception e) {
      log.error("", e);
    }

    return false;
  }

  public void disconnect() {
    persistence = false;
    cancel.countDown();
    if (future != null) {
      future.channel().disconnect();
    }
  }

  public void destroy() {
    eventLoop.shutdownGracefully();
  }

  @Override
  public void propertyChange(PropertyChangeEvent evt) {
    if (evt.getPropertyName().equals(ConnectionObservable.PROPERTY)) {
      final Boolean connected = (Boolean) evt.getNewValue();

      if (!connected && persistence) {
        connect();
      }
    }
  }

  public WebSocketClientHandler getWebSocketHandler() {
    return future.channel().attr(ClientBuilder.WS_HANDLER).get();
  }

  @SuppressWarnings("unused")
  public static class ClientBuilder {
    private static final AttributeKey<WebSocketClientHandler> WS_HANDLER = AttributeKey.valueOf(
        "WS_HANDLER");

    public Client adminBuild() {
      Client client = init();

      client.eventLoop = new NioEventLoopGroup(client.nThreads);
      client.bootstrap = new Bootstrap()
          .group(client.eventLoop)
          .remoteAddress(client.host, client.port)
          .channel(NioSocketChannel.class)
          .option(ChannelOption.SO_KEEPALIVE, true)
          .option(ChannelOption.SO_REUSEADDR, true)
          .handler(new ChannelInitializer<>() {
            @Override
            protected void initChannel(@NotNull Channel ch) {
              ch.pipeline().addLast(
                  new TbMessageDecoder(),
                  new JsonMessageEncoder()
              );
            }
          });

      return client;
    }

    public Client sysMasterBuild() throws URISyntaxException {
      Client client = init();

      URI uri = new URI("ws://" + client.host + ":" + client.port);

      final WebSocketClientHandler handler =
          new WebSocketClientHandler(
              WebSocketClientHandshakerFactory.newHandshaker(
                  uri, WebSocketVersion.V13, null, true, new DefaultHttpHeaders()));

      client.eventLoop = new NioEventLoopGroup(client.nThreads);
      client.bootstrap = new Bootstrap()
          .group(client.eventLoop)
          .remoteAddress(client.host, client.port)
          .channel(NioSocketChannel.class)
          .handler(new ChannelInitializer<>() {
            @Override
            protected void initChannel(@NotNull Channel ch) {
              ch.attr(WS_HANDLER).set(handler);
              ch.pipeline()
                  .addLast(
                      new HttpClientCodec(),
                      new HttpObjectAggregator(64 * 1024),
                      WebSocketClientCompressionHandler.INSTANCE)
                  .addLast(WebSocketClientHandler.class.getName(), handler);
            }
          });

      return client;

    }

  }
}
