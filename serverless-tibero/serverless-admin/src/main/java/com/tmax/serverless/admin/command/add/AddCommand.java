package com.tmax.serverless.admin.command.add;

import com.tmax.serverless.admin.command.SuperCommand;
import picocli.CommandLine.Command;

@Command(
    name = "add",
    synopsisSubcommandLabel = "(DB | Group)",
    subcommands = {
        AddDBCommand.class,
        AddGroupCommand.class
    },
    description = "add DB or Group\n"
)
public class AddCommand extends SuperCommand {

}
