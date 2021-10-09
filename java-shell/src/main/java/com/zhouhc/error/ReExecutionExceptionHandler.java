package com.zhouhc.error;

import picocli.CommandLine;
import picocli.CommandLine.*;

public class ReExecutionExceptionHandler implements IExecutionExceptionHandler {
    @Override
    public int handleExecutionException(Exception ex, CommandLine commandLine, ParseResult parseResult) throws Exception {
        ReExceptionsHandler.ExecutionExceptionHandler(ex, commandLine, parseResult);
        return 1;
    }
}
