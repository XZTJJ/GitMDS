package com.zhouhc.command;


import com.zhouhc.command.mixin.HelpOption;
import com.zhouhc.command.subCommand.TBLFileTOManyJsonFileCommand;
import com.zhouhc.command.subCommand.TBLFileToJsonFileCommand;
import picocli.CommandLine;

@CommandLine.Command(name = "", synopsisSubcommandLabel = "command", description = "detailed message",
        //可能数量不确定是多少,官方推荐这么写
        subcommands = {
                TBLFileTOManyJsonFileCommand.class, TBLFileToJsonFileCommand.class
        })
public class TopCommand {

    //帮助信息
    @CommandLine.Mixin
    private HelpOption helpOption;

}
