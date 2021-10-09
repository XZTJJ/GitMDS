package com.zhouhc.run;


import com.zhouhc.command.TopCommand;
import com.zhouhc.error.ReExecutionExceptionHandler;
import com.zhouhc.error.ReParameterExceptionHandler;
import picocli.CommandLine;

//Command启动类，也就是顶级命令
public class CommandRunner {

    private static CommandLine commandLine;

    //启动顶级的命令
    public static void main(String[] args) {

        //顶级命令
        TopCommand topCommand = new TopCommand();
        //命令行的一些设置
        commandLine = new CommandLine(topCommand);
        //打印信息的长度调整
        commandLine.setUsageHelpAutoWidth(true);
        commandLine.setUsageHelpLongOptionsMaxWidth(40);
        //复写处理逻辑处理
        commandLine.setParameterExceptionHandler(new ReParameterExceptionHandler());
        commandLine.setExecutionExceptionHandler(new ReExecutionExceptionHandler());
        int exitCode = commandLine.execute(args);
        System.exit(exitCode);
    }


    //获取commandLine的实例
    public static CommandLine getCommandLine() {
        return commandLine;
    }


}
