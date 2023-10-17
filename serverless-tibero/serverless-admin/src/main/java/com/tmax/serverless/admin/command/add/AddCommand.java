package com.tmax.serverless.admin.command.add;

import com.tmax.serverless.admin.command.SuperCommand;
import picocli.CommandLine.Command;

@Command(
    name = "add",
    synopsisSubcommandLabel = "(Active | Warm-Up)",
    subcommands = {
        AddDBCommand.class
    },
    description = "add Active DB or Warm-Up DB\n"
)
public class AddCommand extends SuperCommand {

}
