package com.tmax.serverless.admin.command;

import picocli.CommandLine.Command;
import picocli.CommandLine.Model.CommandSpec;
import picocli.CommandLine.Spec;

@Command(name = "SuperCommand")
public class SuperCommand implements Runnable {

  @Spec
  private CommandSpec spec;

  @Override
  public void run() {
    spec.commandLine().usage(System.out);
    System.exit(0);
  }
}
