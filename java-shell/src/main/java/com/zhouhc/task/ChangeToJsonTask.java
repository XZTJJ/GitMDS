package com.zhouhc.task;

import com.google.gson.JsonObject;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;

//将一个tbl文件转换成一个json文件
public class ChangeToJsonTask implements Callable<Boolean> {
    private final File readFile;
    private final String targetDir;
    private final long limitNum;
    private final String delimiter;
    private final String jsonKey;
    private final String jsonFilePrefix;
    private final int autoIndex;
    private final CountDownLatch countDownLatch;

    //构造函数
    public ChangeToJsonTask(File readFile, String targetDir, long limitNum, String delimiter, String jsonKey, String jsonFilePrefix, int autoIndex, CountDownLatch countDownLatch) {
        this.readFile = readFile;
        this.targetDir = StringUtils.isBlank(targetDir) ? null : targetDir;
        this.limitNum = limitNum;
        this.delimiter = delimiter;
        this.jsonKey = jsonKey;
        this.jsonFilePrefix = jsonFilePrefix;
        this.autoIndex = autoIndex;
        this.countDownLatch = countDownLatch;
    }

    @Override
    public Boolean call() throws Exception {
        try {
            long filelines = Files.lines(Paths.get(readFile.getPath())).count();
            long realRealLine = limitNum;
            if (limitNum <= 0 || limitNum > filelines)
                realRealLine = filelines;
            //json的 key 值
            String[] jsonKeys = StringUtils.split(jsonKey, ",");
            //新建文件
            String newFileName = "";
            if (StringUtils.isBlank(jsonFilePrefix))
                newFileName = StringUtils.substringBeforeLast(readFile.getName(), ".");
            else
                newFileName = String.format("%s-%s", jsonFilePrefix, autoIndex);
            File newFile = new File(targetDir, newFileName + ".json");
            //获取文件迭代器,方便读取文件
            LineIterator fileContents = FileUtils.lineIterator(readFile, "UTF-8");
            //临时缓存对应
            List<String> fileContent = new ArrayList<String>();
            //读取文件
            for (int i = 0; i < realRealLine; i++) {
                //获取文件并且切割
                String next = fileContents.next();
                String[] split = StringUtils.split(next, delimiter);
                JsonObject jsonObject = new JsonObject();
                //生成json对象
                for (int j = 0; j < split.length; j++)
                    jsonObject.addProperty(ArrayUtils.isEmpty(jsonKeys) ? String.format("key-%s", j) : jsonKeys[j], split[j]);
                fileContent.add(String.format("%s", jsonObject.toString()));
                //超过2000行进行文件的写入
                if (fileContent.size() > 2000) {
                    FileUtils.writeLines(newFile, fileContent, true);
                    fileContent.clear();
                }
            }
            if (fileContent.size() > 0)
                FileUtils.writeLines(newFile, fileContent, true);
            System.out.println(String.format("%s is create ..", newFile.getPath()));
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (countDownLatch != null)
                countDownLatch.countDown();
        }
        return null;
    }
}
