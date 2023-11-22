package com.tmax.serverless.admin.command.scale;

import com.tmax.serverless.admin.command.CallableSubCommand;
import com.tmax.serverless.admin.utils.ConsoleColors;
import com.tmax.serverless.admin.utils.ConsoleColors.Styles;
import com.tmax.serverless.core.message.admin.AdminMsgScaleOut;
import com.tmax.serverless.core.message.admin.AdminMsgScaleOutComplete;
import com.tmax.serverless.core.message.admin.AdminMsgScaleOutCompleteReply;
import lombok.extern.slf4j.Slf4j;
import picocli.CommandLine.Command;
import picocli.CommandLine.Model.CommandSpec;
import picocli.CommandLine.Option;
import picocli.CommandLine.Spec;

@Slf4j
@Command(name = "out-complete")
public class ScaleOutCompleteCommand extends CallableSubCommand<AdminMsgScaleOutCompleteReply> {
  @Spec
  private CommandSpec spec;

  @Option(
      names = {"--db-name"},
      description = "Database Name",
      required = true)
  private String dbName;

  @Option(
      names = {"--alias"},
      description = "DB Instance alias",
      required = true)
  private String alias;

  @Option(
      names = {"--ip"},
      description = "DB Instance ip",
      required = true)
  private String ip;

  @Option(
      names = {"--port"},
      description = "DB Instance port",
      required = true)
  private int port;

  @Override
  public String getServiceName() {
    return getClass().toString();
  }

  @Override
  public Integer call() {
    log.info("{}", this);
    AdminMsgScaleOutComplete req = AdminMsgScaleOutComplete.builder()
        .dbName(dbName)
        .alias(alias)
        .ip(ip)
        .port(port)
        .build();
    log.info("req msg: " + req);
    return send(req,
        (ctx, res) -> {
          log.info("ScaleOutCompleteCommand result: {}", res);
          printResult(res, String.format("DB(%s) became warm up mode.",
              ConsoleColors.set(alias, Styles.BOLD)));
        });
  }
}
