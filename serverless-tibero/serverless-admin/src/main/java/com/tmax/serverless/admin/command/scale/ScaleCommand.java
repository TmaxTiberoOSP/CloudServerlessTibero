package com.tmax.serverless.admin.command.scale;

import com.tmax.serverless.admin.command.SuperCommand;
import com.tmax.serverless.admin.command.add.AddDBCommand;
import picocli.CommandLine.Command;

@Command(
    name = "scale",
    synopsisSubcommandLabel = "(In | Out | OutComplete)",
    subcommands = {
        ScaleInCommand.class,
        ScaleOutCommand.class,
        ScaleOutCompleteCommand.class
    },
    description = "scale In or Out or OutComplete\n"
)
public class ScaleCommand extends SuperCommand {

}
