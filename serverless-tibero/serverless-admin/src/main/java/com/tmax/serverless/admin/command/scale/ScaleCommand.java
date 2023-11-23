package com.tmax.serverless.admin.command.scale;

import com.tmax.serverless.admin.command.SuperCommand;
import com.tmax.serverless.admin.command.add.AddDBCommand;
import picocli.CommandLine.Command;

@Command(
    name = "scale",
    synopsisSubcommandLabel = "(Out | In | InComplete)",
    subcommands = {
        ScaleOutCommand.class,
        ScaleInCommand.class,
        ScaleInCompleteCommand.class
    },
    description = "scale Out or In or InComplete\n"
)
public class ScaleCommand extends SuperCommand {

}
