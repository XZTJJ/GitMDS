package com.zhouhc.command.subCommand;


import com.zhouhc.command.mixin.HelpOption;
import com.zhouhc.task.JsonFilesTask;
import com.zhouhc.template.ReCommand;
import com.zhouhc.threadcompents.CThreadFactory;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;
import org.apache.commons.lang3.StringUtils;
import picocli.CommandLine;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.*;

/**
 * top下面的 TBLFileTOManyJsonFileCommand 命令 , 主要是将一个文件转换成多个json文件，(比如tbl或者csv 转成多个json文件)
 */
@CommandLine.Command(name = "TblToJsonFiles", description = "tbl file separation many json files, one line is a json file", requiredOptionMarker = '*')
public class TBLFileTOManyJsonFileCommand extends ReCommand {

    @CommandLine.Option(names = {"-sf", "--sourceFile"}, required = true, description = "the tbl file path")
    private File sourceFile;
    @CommandLine.Option(names = {"-dd", "--targetDir"}, required = true, description = "the folder where the json file is located")
    private String targetDir;
    @CommandLine.Option(names = {"-ln", "--limitNum"}, description = "how many lines need changed to json ,default all")
    private int limitNum;
    @CommandLine.Option(names = {"-dl", "--delimiter"}, description = "the delimiter, default is ,")
    private String delimiter;
    @CommandLine.Option(names = {"-jk", "--jsonKey"}, description = "the json Key, use , delimiter ,if no jsonKey, use auto Integer.")
    private String jsonKey;
    @CommandLine.Option(names = {"-tn", "--threadNum"}, description = "create json file threads, default is 3")
    private int threadNum;
    @CommandLine.Option(names = {"-jp", "--jsonFilePrefix"}, description = "the json fileName , form : jn-autoInt.json")
    private String jsonFilePrefix;
    @CommandLine.Option(names = {"-qs", "--queueSize"}, description = "the threadFactory queue size")
    private int queueSize;

    //帮助信息
    @CommandLine.Mixin
    private HelpOption helpOption;

    //获取内容
    @Override
    public Integer dotask() throws Exception {
        System.out.println("starting...");
        long filelines = Files.lines(Paths.get(sourceFile.getPath())).count();
        if (limitNum <= 0 || limitNum > filelines)
            limitNum = (int)filelines;
        LineIterator fileContents = FileUtils.lineIterator(sourceFile, "UTF-8");
        if (StringUtils.isBlank(delimiter))
            delimiter = ",";
        if (threadNum <= 0)
            threadNum = 3;
        if (StringUtils.isBlank(jsonFilePrefix))
            jsonFilePrefix = StringUtils.substringBeforeLast(sourceFile.getName(), ".");
        //创建线程池，设置计数
        if (queueSize <= 0)
            queueSize = 30000;
        ExecutorService executorService = new ThreadPoolExecutor(threadNum, threadNum, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>(queueSize), new CThreadFactory("file-handler"),new ThreadPoolExecutor.CallerRunsPolicy());
        CountDownLatch countDownLatch = new CountDownLatch(limitNum);
        int mark = 0;
        while (mark < limitNum) {
            executorService.submit(new JsonFilesTask(fileContents.next(), targetDir, delimiter, jsonKey, jsonFilePrefix, mark + 1, countDownLatch));
            mark += 1;
        }
        countDownLatch.await();
        executorService.shutdown();
        System.out.println("finish...");
        return 0;
    }

}
