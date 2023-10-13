package com.tmax.serverless.admin.command;

import static com.tmax.serverless.admin.utils.ConsoleColors.Colors.GREEN;
import static com.tmax.serverless.admin.utils.ConsoleColors.Colors.RED;
import static com.tmax.serverless.admin.utils.ConsoleColors.Styles.BOLD;

import com.tmax.serverless.admin.Global;
import com.tmax.serverless.admin.utils.ConsoleColors;
import com.tmax.serverless.core.handler.Callback;
import com.tmax.serverless.core.handler.CallbackHandler;
import com.tmax.serverless.core.message.JsonMessage;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import java.lang.reflect.ParameterizedType;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import lombok.extern.slf4j.Slf4j;
import picocli.CommandLine.Command;

@Slf4j
@Command(name = "CallableSubCommand")
public abstract class CallableSubCommand<T extends JsonMessage> implements Callable<Integer> {

  private final Class<T> responseMessageClass;

  @SuppressWarnings("unchecked")
  public CallableSubCommand() {
    responseMessageClass = (Class<T>) ((ParameterizedType) getClass()
        .getGenericSuperclass()).getActualTypeArguments()[0];
  }

  public abstract String getServiceName();

  public Integer send(JsonMessage request, int timeoutSec, Callback<T> callback) {
    CompletableFuture<Integer> promise = new CompletableFuture<>();

    if (log.isDebugEnabled()) {
      log.info("{}", request);
    }

    Channel ch = Global.getInstance().getClient().getFuture().channel();

    CallbackHandler.add(ch,
        new CallbackHandler<T>(
            getServiceName()
        ) {
          @Override
          public boolean callback(ChannelHandlerContext ctx, Object message) {
            String responseResult = ((JsonMessage) message).getResult().toString();
            if (log.isDebugEnabled()) {
              log.info("{}", responseResult);
            }

            if (responseResult != null) {
              callback.run(ctx, (JsonMessage)message);
              promise.complete(0);
            } else {
              log.error("Unknown message: {}", message);
              promise.complete(1);
            }

            return false;
          }
        });

    ch.writeAndFlush(request);

    try {
      return promise.get(timeoutSec, TimeUnit.SECONDS);
    } catch (InterruptedException | ExecutionException | TimeoutException e) {
      log.error("", e);
      if (e instanceof TimeoutException) {
        System.err.println(ConsoleColors.set("FAIL", BOLD, RED) + " - Time-out");
      }
      return 1;
    }
  }

  public Integer send(JsonMessage request, Callback<T> callback) {
    return send(request, 30, callback);
  }

  public void printSuccess(JsonMessage res, String message) {
    System.out.println((res.isSuccess()
        ? ConsoleColors.set("SUCCESS", BOLD, GREEN)
        : ConsoleColors.set("FAIL", BOLD, RED)) +
        " - " + message);
  }

  public void printResult(JsonMessage res, String message) {
    printSuccess(res, message);
    if (!res.isSuccess()) {
//      System.err.println("code: " + res.getErrorCode());
//      System.err.println("message: " + res.getErrorMessage());
    }
  }
}
