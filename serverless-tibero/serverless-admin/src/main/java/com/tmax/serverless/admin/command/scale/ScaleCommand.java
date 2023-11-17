package com.tmax.serverless.admin.command.scale;

import com.tmax.serverless.admin.command.SuperCommand;
import com.tmax.serverless.admin.command.add.AddDBCommand;
import picocli.CommandLine.Command;

@Command(
    name = "scale",
    synopsisSubcommandLabel = "(In | Out)",
    subcommands = {
        ScaleInCommand.class,
        ScaleOutCommand.class
    },
    description = "Scale In or Scale Out\n"
)
public class ScaleCommand extends SuperCommand {

}
