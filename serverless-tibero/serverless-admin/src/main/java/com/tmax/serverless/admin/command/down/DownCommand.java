package com.tmax.serverless.admin.command.down;

import com.tmax.serverless.admin.command.SuperCommand;
import com.tmax.serverless.admin.command.boot.BootDBCommand;
import picocli.CommandLine.Command;

@Command(
    name = "down",
    synopsisSubcommandLabel = "(MANAGER)",
    subcommands = {
        //DownDBCommand.class,
        DownManagerCommand.class
    },
    description = "down Manager\n")
public class DownCommand extends SuperCommand {

}
