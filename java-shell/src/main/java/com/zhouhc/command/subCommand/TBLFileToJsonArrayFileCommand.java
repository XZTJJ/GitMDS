package com.zhouhc.command.subCommand;


import com.zhouhc.command.mixin.HelpOption;
import com.zhouhc.task.ChangeToJsonArrayTask;
import com.zhouhc.template.ReCommand;
import com.zhouhc.threadcompents.CThreadFactory;
import picocli.CommandLine;

import java.io.File;
import java.util.concurrent.*;

/**
 * top下面的 TBLFileTOManyJsonFileCommand 命令 , 主要是将一个文件转换成多个json文件，(比如tbl或者csv 转成多个json文件)
 */
@CommandLine.Command(name = "TBLFileToJsonArrayFile", description = "tbl file change to json file, use array style,", requiredOptionMarker = '*')
public class TBLFileToJsonArrayFileCommand extends ReCommand {

    @CommandLine.Option(names = {"-sf", "--sourceFile"}, required = true, description = "the tbl file path")
    private File sourceFile;
    @CommandLine.Option(names = {"-dd", "--targetDir"}, required = true, description = "the folder where the json file is located")
    private String targetDir;
    @CommandLine.Option(names = {"-ln", "--limitNum"}, description = "how many lines need changed to json ,default all")
    private int limitNum;
    @CommandLine.Option(names = {"-dl", "--delimiter"}, description = "the delimiter, default is ,", defaultValue = ",")
    private String delimiter;
    @CommandLine.Option(names = {"-jok", "--jsonObjectKey"}, description = "the object of json array item Key, use , delimiter ,if no jsonKey, use auto Integer.")
    private String jsonObjectKey;
    @CommandLine.Option(names = {"-jak", "--jsonArrayKey"}, description = "the json array Key, use , delimiter ,if no jsonKey, use auto Integer.", defaultValue = "RECORDS")
    private String jsonArrayKey;
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
        for (int i = 0; i < filelists.length; i++)
            executorService.submit(new ChangeToJsonArrayTask(filelists[i], targetDir, limitNum, delimiter, jsonObjectKey, jsonArrayKey, jsonFilePrefix, i, countDownLatch));
        countDownLatch.await();
        executorService.shutdown();
        System.out.println("finish...");
        return 0;
    }

}
