package com.tmax.serverless.admin.command.add;

import com.tmax.serverless.admin.cli.DBServerlessModeOption;
import com.tmax.serverless.admin.command.CallableSubCommand;
import com.tmax.serverless.admin.utils.ConsoleColors;
import com.tmax.serverless.admin.utils.ConsoleColors.Styles;
import com.tmax.serverless.core.context.DBServerlessMode;
import com.tmax.serverless.core.message.admin.AdminMsgAddDB;
import com.tmax.serverless.core.message.admin.AdminMsgAddDBReply;
import java.util.Locale;
import lombok.extern.slf4j.Slf4j;
import picocli.CommandLine.Command;
import picocli.CommandLine.Model.CommandSpec;
import picocli.CommandLine.Option;
import picocli.CommandLine.Spec;

@Slf4j
@Command(name = "db")
public class AddDBCommand extends CallableSubCommand<AdminMsgAddDBReply> {
  @Spec
  private CommandSpec spec;

  @Option(
      names = {"--db-name"},
      description = "Database Name",
      required = true)
  private String dbName;

  @Option(
      names = {"--id"},
      description = "DB Instance id",
      required = true)
  private String id;

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

  @Option(
      names = {"--db-user"},
      description = "DB Instance user",
      required = true)
  private String dbUser;

  @Option(
      names = {"--db-pw"},
      description = "DB Instance password",
      required = true)
  private String dbPassword;

  @Option(
      names = {"--pod-name"},
      description = "Name of DB Pod",
      required = true)
  private String podName;

  @Option(
      names = {"--mode"},
      completionCandidates = DBServerlessModeOption.Candidates.class,
      converter = DBServerlessModeOption.Converter.class,
      description = "DB Serverless Mode\n"
          + "[${COMPLETION-CANDIDATES}]",
      required = true)
  private DBServerlessMode mode;

  @Override
  public String getServiceName() {
    return getClass().toString();
  }

  @Override
  public Integer call() {
    log.info("{}", this);
    AdminMsgAddDB req = AdminMsgAddDB.builder()
        .dbName(dbName)
        .id(id)
        .alias(alias)
        .ip(ip)
        .port(port)
        .dbUser(dbUser)
        .dbPassword(dbPassword)
        .podName(podName)
        .mode(mode)
        .build();
    log.info("req msg: " + req);
    return send(req,
        (ctx, res) -> {
          log.info("AddDBCommand result: {}", res);
          printResult(res, String.format("add DB(%s:%s) on %s mode",
              ConsoleColors.set(alias, Styles.BOLD),
              ConsoleColors.set(id, Styles.BOLD),
              mode.toString()));
        });
  }

}
