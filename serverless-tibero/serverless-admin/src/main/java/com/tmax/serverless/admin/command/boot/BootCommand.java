package com.tmax.serverless.admin.command.boot;

import com.tmax.serverless.admin.command.SuperCommand;
import picocli.CommandLine.Command;

@Command(
    name = "boot",
    synopsisSubcommandLabel = "(node | master-vdb)",
    subcommands = {
        BootNodeCommand.class
    },
    description = "boot node, master-vdb\n")
public class BootCommand extends SuperCommand {

}
