package com.tmax.serverless.admin.command.add;

import com.tmax.serverless.admin.cli.DBServerlessModeOption;
import com.tmax.serverless.admin.command.CallableSubCommand;
import com.tmax.serverless.admin.utils.ConsoleColors;
import com.tmax.serverless.admin.utils.ConsoleColors.Styles;
import com.tmax.serverless.core.context.DBServerlessMode;
import com.tmax.serverless.core.message.admin.AdminMsgAddDB;
import com.tmax.serverless.core.message.admin.AdminMsgAddDBReply;
import com.tmax.serverless.core.message.admin.AdminMsgAddGroup;
import com.tmax.serverless.core.message.admin.AdminMsgAddGroupReply;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import picocli.CommandLine.Command;
import picocli.CommandLine.Model.CommandSpec;
import picocli.CommandLine.Option;
import picocli.CommandLine.Spec;

@Slf4j
@Command(name = "group")
public class AddGroupCommand extends CallableSubCommand<AdminMsgAddGroupReply> {
  @Spec
  private CommandSpec spec;

  @Option(
      names = {"--name"},
      description = "Monitoring Group Name",
      required = true)
  private String groupName;

//  @Option(
//      names = {"--alias-list"},
//      description = "DB Instance alias list for Monitoring",
//      required = true)
//  private List<String> aliasList;

  @Override
  public String getServiceName() {
    return getClass().toString();
  }

  @Override
  public Integer call() {
    log.info("{}", this);
    AdminMsgAddGroup req = AdminMsgAddGroup.builder()
        .groupName(groupName)
        //.aliasList(aliasList)
        .build();
    log.info("req msg: " + req);
    return send(req,
        (ctx, res) -> {
          log.info("AddDBCommand result: {}", res);
          printResult(res, String.format("add group(%s) on Monitoring mode",
              ConsoleColors.set(groupName, Styles.BOLD)));
        });
  }
}
