package com.tmax.serverless.admin.command.down;

import com.tmax.serverless.admin.command.CallableSubCommand;
import com.tmax.serverless.core.message.admin.AdminMsgDownManager;
import com.tmax.serverless.core.message.admin.AdminMsgDownManagerReply;
import lombok.extern.slf4j.Slf4j;
import picocli.CommandLine.Command;

@Slf4j
@Command(name = "manager")
public class DownManagerCommand extends CallableSubCommand<AdminMsgDownManagerReply> {

  @Override
  public String getServiceName() {
    return getClass().toString();
  }

  @Override
  public Integer call() {
    log.info("{}", this);

    AdminMsgDownManager req = AdminMsgDownManager.builder()
        .build();
    log.info("req msg: " + req);

    return send(req,
        (ctx, res) -> {
          log.info("DownManagerCommand result:", res);
          printResult(res, String.format("down manager."));
        });
  }
}
