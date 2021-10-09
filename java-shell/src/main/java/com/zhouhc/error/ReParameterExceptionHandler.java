package com.zhouhc.error;

import picocli.CommandLine.IParameterExceptionHandler;
import picocli.CommandLine.ParameterException;

//重写 picocile 错误解析处理方法
public class ReParameterExceptionHandler implements IParameterExceptionHandler {
    //错误处理方法
    @Override
    public int handleParseException(ParameterException ex, String[] args) throws Exception {
        ReExceptionsHandler.ParameterExceptionHandler(ex, args);
        return 2;
    }
}
