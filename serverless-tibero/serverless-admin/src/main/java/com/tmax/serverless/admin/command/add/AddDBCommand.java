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
      names = {"--alias"},
      description = "DB Instance alias",
      required = true)
  private String alias;

  @Option(
      names = {"-m", "--mode"},
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
        .alias(alias)
        .mode(mode)
        .build();
    log.info("req msg: " + req);
    return send(req,
        (ctx, res) -> {
          log.info("{}", res);
          printResult(res, String.format("add DB(%s) on %s mode",
              ConsoleColors.set(alias, Styles.BOLD),
              mode.toString()));
        });
  }

}
