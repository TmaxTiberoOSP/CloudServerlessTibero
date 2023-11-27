package com.tmax.serverless.admin.command.boot;

import com.tmax.serverless.admin.command.SuperCommand;
import picocli.CommandLine.Command;

@Command(
    name = "boot",
    synopsisSubcommandLabel = "(Manager)",
    subcommands = {
        // BootDBCommand.class
        //BootManagerCommand.class
    },
    description = "boot Manager\n")
public class BootCommand extends SuperCommand {

}
