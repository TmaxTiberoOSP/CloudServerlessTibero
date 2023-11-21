package com.tmax.serverless.admin.command.boot;

import com.tmax.serverless.admin.command.SuperCommand;
import picocli.CommandLine.Command;

@Command(
    name = "boot",
    synopsisSubcommandLabel = "(DB)",
    subcommands = {
        BootDBCommand.class
    },
    description = "boot db\n")
public class BootCommand extends SuperCommand {

}
