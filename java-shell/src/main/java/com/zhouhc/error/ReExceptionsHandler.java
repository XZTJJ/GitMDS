package com.zhouhc.error;

import picocli.CommandLine;
import picocli.CommandLine.*;

import java.io.PrintWriter;

//重新复写错误处理
public class ReExceptionsHandler {


    //picocli解析错误处理方式
    public static void ParameterExceptionHandler(ParameterException ex, String[] args) {
        //获取命令 和 err的错误处理
        CommandLine cmd = ex.getCommandLine();
        PrintWriter err = cmd.getErr();

        if (ex instanceof MaxValuesExceededException) {
            err.println(cmd.getColorScheme().errorText(String.format("parse error : %s", ex.getMessage())));
        } else if (ex instanceof MissingParameterException) {
            err.println(cmd.getColorScheme().errorText(String.format("parse error : %s", ex.getMessage())));
        } else if (ex instanceof MissingTypeConverterException) {
            err.println(cmd.getColorScheme().errorText(String.format("parse error : %s", ex.getMessage())));
        } else if (ex instanceof MutuallyExclusiveArgsException) {
            err.println(cmd.getColorScheme().errorText(String.format("parse error : %s", ex.getMessage())));
        } else if (ex instanceof OverwrittenOptionException) {
            err.println(cmd.getColorScheme().errorText(String.format("parse error : %s", ex.getMessage())));
        } else if (ex instanceof UnmatchedArgumentException) {
            err.println(cmd.getColorScheme().errorText(String.format("parse error : %s", ex.getMessage())));
            //((UnmatchedArgumentException) ex).printSuggestions(err);
        } else {
            err.println(cmd.getColorScheme().errorText(String.format("parse error : %s", ex.getMessage())));
        }
        //打印对应版主信息
        cmd.usage(System.out);
    }


    //业务逻辑处理处理
    public static void ExecutionExceptionHandler(Exception ex, CommandLine cmd, ParseResult parseResult) {
        PrintWriter err = cmd.getErr();
        err.println(cmd.getColorScheme().errorText(String.format("invoke error : %s", ex.getMessage())));
        //是否需要打印错误日志
        if ("true".equals(System.getProperty("isPrintError"))) {
            ex.printStackTrace();
        }
    }
}
