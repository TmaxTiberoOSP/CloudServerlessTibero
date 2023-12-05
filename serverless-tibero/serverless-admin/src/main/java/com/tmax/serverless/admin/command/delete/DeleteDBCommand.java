package com.tmax.serverless.admin.command.delete;

import com.tmax.serverless.admin.cli.DBServerlessModeOption;
import com.tmax.serverless.admin.command.CallableSubCommand;
import com.tmax.serverless.admin.utils.ConsoleColors;
import com.tmax.serverless.admin.utils.ConsoleColors.Styles;
import com.tmax.serverless.core.context.DBServerlessMode;
import com.tmax.serverless.core.message.admin.AdminMsgAddDB;
import com.tmax.serverless.core.message.admin.AdminMsgDeleteDB;
import com.tmax.serverless.core.message.admin.AdminMsgDeleteDBReply;
import lombok.extern.slf4j.Slf4j;
import picocli.CommandLine.Command;
import picocli.CommandLine.Model.CommandSpec;
import picocli.CommandLine.Option;
import picocli.CommandLine.Spec;

@Slf4j
@Command(name = "db")
public class DeleteDBCommand extends CallableSubCommand<AdminMsgDeleteDBReply> {
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


  @Override
  public String getServiceName() {
    return getClass().toString();
  }

  @Override
  public Integer call() {
    log.info("{}", this);
    AdminMsgDeleteDB req = AdminMsgDeleteDB.builder()
        .dbName(dbName)
        .alias(alias)
        .build();
    log.info("req msg: " + req);
    return send(req,
        (ctx, res) -> {
          log.info("DeleteDBCommand result: {}", res);
          printResult(res, String.format("delete DB(%s:%s)",
              ConsoleColors.set(alias, Styles.BOLD)));
        });
  }
}
