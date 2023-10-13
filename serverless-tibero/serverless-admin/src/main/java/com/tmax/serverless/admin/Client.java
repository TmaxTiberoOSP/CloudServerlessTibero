package com.tmax.serverless.admin;

import com.tmax.serverless.core.handler.ConnectionObservable;
import com.tmax.serverless.core.handler.codec.JsonMessageEncoder;
import com.tmax.serverless.core.handler.codec.TbMessageDecoder;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
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


  @SuppressWarnings("unused")
  public static class ClientBuilder {

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

  }
}
