package com.tmax.serverless.admin.command.boot;

import com.tmax.serverless.admin.command.SuperCommand;
import picocli.CommandLine.Command;

@Command(
    name = "boot",
    synopsisSubcommandLabel = "(node)",
    subcommands = {
        BootNodeCommand.class
    },
    description = "boot node\n")
public class BootCommand extends SuperCommand {

}
