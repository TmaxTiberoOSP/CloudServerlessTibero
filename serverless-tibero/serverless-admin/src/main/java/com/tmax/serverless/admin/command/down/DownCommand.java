package com.tmax.serverless.admin.command.down;

import com.tmax.serverless.admin.command.SuperCommand;
import com.tmax.serverless.admin.command.boot.BootDBCommand;
import picocli.CommandLine.Command;

@Command(
    name = "down",
    synopsisSubcommandLabel = "(DB)",
    subcommands = {
        BootDBCommand.class
    },
    description = "down db\n")
public class DownCommand extends SuperCommand {

}
