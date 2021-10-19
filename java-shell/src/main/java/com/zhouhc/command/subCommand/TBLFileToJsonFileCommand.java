package com.zhouhc.command.subCommand;


import com.zhouhc.command.mixin.HelpOption;
import com.zhouhc.task.ChangeToJsonTask;
import com.zhouhc.template.ReCommand;
import com.zhouhc.threadcompents.CThreadFactory;
import org.apache.commons.lang3.StringUtils;
import picocli.CommandLine;

import java.io.File;
import java.util.concurrent.*;

/**
 * top下面的 TBLFileTOManyJsonFileCommand 命令 , 主要是将一个文件转换成多个json文件，(比如tbl或者csv 转成多个json文件)
 */
@CommandLine.Command(name = "TblToJsonFile", description = "tbl file change to json,", requiredOptionMarker = '*')
public class TBLFileToJsonFileCommand extends ReCommand {

    @CommandLine.Option(names = {"-sf", "--sourceFile"}, required = true, description = "the tbl file path")
    private File sourceFile;
    @CommandLine.Option(names = {"-dd", "--targetDir"}, required = true, description = "the folder where the json file is located")
    private String targetDir;
    @CommandLine.Option(names = {"-ln", "--limitNum"}, description = "how many lines need changed to json ,default all")
    private int limitNum;
    @CommandLine.Option(names = {"-dl", "--delimiter"}, description = "the delimiter, default is ,", defaultValue = ",")
    private String delimiter;
    @CommandLine.Option(names = {"-jk", "--jsonKey"}, description = "the json Key, use , delimiter ,if no jsonKey, use auto Integer.")
    private String jsonKey;
    @CommandLine.Option(names = {"-tn", "--threadNum"}, description = "create json file threads, default is 3", defaultValue = "3", fallbackValue = "3")
    private int threadNum;
    @CommandLine.Option(names = {"-jp", "--jsonFilePrefix"}, description = "the json fileName , form : jn-autoInt.json,default is sourcefileName")
    private String jsonFilePrefix;
    @CommandLine.Option(names = {"-qs", "--queueSize"}, description = "the threadFactory queue size", defaultValue = "30000")
    private int queueSize;

    //帮助信息
    @CommandLine.Mixin
    private HelpOption helpOption;

    //获取内容
    @Override
    public Integer dotask() throws Exception {
        System.out.println("starting...");
        File[] filelists = null;
        //判断是否为目录
        if (sourceFile.isDirectory())
            filelists = sourceFile.listFiles();
        else
            filelists = new File[]{sourceFile};
        //设置对应的线程数
        ExecutorService executorService = new ThreadPoolExecutor(threadNum, threadNum, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>(queueSize), new CThreadFactory("file-handler"), new ThreadPoolExecutor.CallerRunsPolicy());
        CountDownLatch countDownLatch = new CountDownLatch(filelists.length);
        for (int i = 0;i<filelists.length;i++)
            executorService.submit(new ChangeToJsonTask(filelists[i], targetDir, limitNum, delimiter, jsonKey, jsonFilePrefix,i, countDownLatch));
        countDownLatch.await();
        executorService.shutdown();
        System.out.println("finish...");
        return 0;
    }

}
