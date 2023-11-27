package com.tmax.serverless.admin.command;

import static com.tmax.serverless.core.config.ServerlessConst.SM_IP;
import static com.tmax.serverless.core.config.ServerlessConst.SM_PORT;

import com.tmax.serverless.admin.Global;
import com.tmax.serverless.admin.cli.LogLevelOption.Candidates;
import com.tmax.serverless.admin.cli.LogLevelOption.Converter;
import com.tmax.serverless.admin.command.add.AddCommand;
import com.tmax.serverless.admin.command.down.DownCommand;
import com.tmax.serverless.admin.command.scale.ScaleCommand;
import com.tmax.serverless.core.Client;
import com.tmax.serverless.core.config.ServerlessConst;
import com.tmax.serverless.core.log.Logger;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.Level;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Model.CommandSpec;
import picocli.CommandLine.Option;
import picocli.CommandLine.ParameterException;
import picocli.CommandLine.ParseResult;
import picocli.CommandLine.Spec;

@Slf4j
@ToString
@Command(
    name = "smcli",
    subcommands = {
        AddCommand.class,
        //DeleteCommand.class,
        ScaleCommand.class,
        //BootCommand.class,
        DownCommand.class
    })
public class MainCommand implements Runnable {

  @Spec
  public CommandSpec spec;

  @Option(
      names = {"--help"},
      usageHelp = true,
      description = "display this help message")
  boolean usageHelpRequested;

  /* TBM Front address */
  @Option(
      names = {"-h", "--host"},
      description = "Tibero Serverless Manager IP address (default: ${DEFAULT-VALUE})")
  private String host = SM_IP;

  @Option(
      names = {"-p", "--port"},
      description = "Tibero Serverless Port (default: ${DEFAULT-VALUE})")
  private int port = SM_PORT;

  public String getFeAddress() {
    return host + ":" + port;
  }

  /* Log level */
  @Option(
      names = {"-v", "--log-level"},
      completionCandidates = Candidates.class,
      converter = Converter.class,
      description = "Log level (default: ${DEFAULT-VALUE})\n"
          + "[${COMPLETION-CANDIDATES}]")
  private Level logLevel = Level.INFO;

  public int executionStrategy(ParseResult parseResult) {
    boolean connected;

    try {
      /* initialization logger */
      String path = ServerlessConst.SM_HOME + "/instance/serverless/admin/";
      String filename = path + "/admin.log";
      String filePattern = path + Logger.DEFAULT_DATE_PATTERN + "/admin.log";

      Logger.initRollingFileLogger(logLevel, "com/tmax/serverless/admin", filename, filePattern);

      log.info("{}", this);

      /* initialization connection */
      Client client = Client.builder()
          .host(host)
          .port(port)
          .adminBuild();
      Global.getInstance().setClient(client);

      log.info("{}", client);
      connected = client.connect();
      if (connected) {
        return new CommandLine.RunLast().execute(parseResult);
      }
    } catch (Exception e) {
      e.printStackTrace();
      return 1;
    }

    log.error("connection failed");
    throw new ParameterException(spec.commandLine(),
        String.format("Invalid address '%s' for options '--host', '--port': "
            + "connection failed", getFeAddress()));
  }

  public void run() {
    spec.commandLine().usage(System.out);
    System.exit(0);
  }
}
