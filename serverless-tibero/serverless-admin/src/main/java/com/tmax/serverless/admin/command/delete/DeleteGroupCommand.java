package com.tmax.serverless.admin.command.delete;

import com.tmax.serverless.admin.command.CallableSubCommand;
import com.tmax.serverless.admin.utils.ConsoleColors;
import com.tmax.serverless.admin.utils.ConsoleColors.Styles;
import com.tmax.serverless.core.message.admin.AdminMsgAddGroup;
import com.tmax.serverless.core.message.admin.AdminMsgDeleteDBReply;
import com.tmax.serverless.core.message.admin.AdminMsgDeleteGroup;
import com.tmax.serverless.core.message.admin.AdminMsgDeleteGroupReply;
import lombok.extern.slf4j.Slf4j;
import picocli.CommandLine.Command;
import picocli.CommandLine.Model.CommandSpec;
import picocli.CommandLine.Option;
import picocli.CommandLine.Spec;

@Slf4j
@Command(name = "group")
public class DeleteGroupCommand extends CallableSubCommand<AdminMsgDeleteGroupReply> {
  @Spec
  private CommandSpec spec;

  @Option(
      names = {"--name"},
      description = "Monitoring Group Name",
      required = true)
  private String groupName;

  @Override
  public String getServiceName() {
    return getClass().toString();
  }

  @Override
  public Integer call() {
    log.info("{}", this);
    AdminMsgDeleteGroup req = AdminMsgDeleteGroup.builder()
        .groupName(groupName)
        .build();
    log.info("req msg: " + req);
    return send(req,
        (ctx, res) -> {
          log.info("DeleteGroupCommand result: {}", res);
          printResult(res, String.format("delete group(%s)",
              ConsoleColors.set(groupName, Styles.BOLD)));
        });
  }
}
