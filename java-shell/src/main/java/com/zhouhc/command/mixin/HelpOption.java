package com.zhouhc.command.mixin;

import picocli.CommandLine.Option;

/**
 * 帮助信息选项
 */
public class HelpOption {
    @Option(names = {"-h", "--help"}, usageHelp = true, description = "print this help")
    private boolean help;
}
