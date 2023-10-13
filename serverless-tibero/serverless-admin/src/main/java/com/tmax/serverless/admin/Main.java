package com.tmax.serverless.admin;

import com.tmax.serverless.admin.command.MainCommand;
import picocli.CommandLine;

public class Main {

  public static void main(String[] args) {
    MainCommand cmd = new MainCommand();
    int exitCode = new CommandLine(cmd)
        .setExecutionStrategy(cmd::executionStrategy)
        .execute(args);

    Client client = Global.getInstance().getClient();
    if (client != null) {
      client.disconnect();
      client.destroy();
    }

    System.exit(exitCode);
  }
}
