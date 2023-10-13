package com.tmax.serverless.admin.command.boot;

import static com.tmax.serverless.admin.utils.ConsoleColors.Styles.BOLD;

import com.tmax.serverless.admin.command.CallableSubCommand;
import com.tmax.serverless.admin.utils.ConsoleColors;
import com.tmax.serverless.core.message.admin.AdminMsgDbBoot;
import com.tmax.serverless.core.message.admin.AdminMsgDbBootReply;
import lombok.extern.slf4j.Slf4j;
import picocli.CommandLine.Command;
import picocli.CommandLine.Model.CommandSpec;
import picocli.CommandLine.Option;
import picocli.CommandLine.Spec;

@Slf4j
@Command(name = "node")
public class BootNodeCommand extends CallableSubCommand<AdminMsgDbBootReply> {

  @Spec
  private CommandSpec spec;

  @Option(
      names = {"--alias"},
      description = "DB instance alias",
      required = true)
  private String alias;


  @Override
  public String getServiceName() {
    return "com.tmax.serverless.manager.BootNodeService";
  }

  @Override
  public Integer call() {
    log.info("{}", this);

    return send(AdminMsgDbBoot.builder()
            .nodeName(alias)
            .build(),
        (ctx, res) -> {
          log.info("{}", res);
          printResult(res, String.format("boot instance(%s)",
              ConsoleColors.set(alias, BOLD)));
        });
  }

}
