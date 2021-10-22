package com.zhouhc.task;

import com.google.gson.JsonObject;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;

//将一个tbl文件转换成一个json文件
public class ChangeToJsonArrayTask implements Callable<Boolean> {
    private final File readFile;
    private final String targetDir;
    private final long limitNum;
    private final String delimiter;
    private final String jsonObjectKey;
    private final String jsonArrayKey;
    private final String jsonFilePrefix;
    private final int autoIndex;
    private final CountDownLatch countDownLatch;

    //构造函数
    public ChangeToJsonArrayTask(File readFile, String targetDir, long limitNum, String delimiter, String jsonObjectKey, String jsonArrayKey, String jsonFilePrefix, int autoIndex, CountDownLatch countDownLatch) {
        this.readFile = readFile;
        this.targetDir = StringUtils.isBlank(targetDir) ? null : targetDir;
        this.limitNum = limitNum;
        this.delimiter = delimiter;
        this.jsonObjectKey = jsonObjectKey;
        this.jsonArrayKey = jsonArrayKey;
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
            //json数组中每个json的Key
            String[] jsonObjectKeyArrays = StringUtils.split(jsonObjectKey, ",");
            //json数组的Key
            String[] jsonArrayKeyArrays = StringUtils.split(jsonArrayKey, ",");
            //每个json数组的报错的元素的个数
            int arraysSize = (int) (realRealLine / jsonArrayKeyArrays.length);
            //新建文件
            String newFileName = "";
            if (StringUtils.isBlank(jsonFilePrefix))
                newFileName = StringUtils.substringBeforeLast(readFile.getName(), ".");
            else
                newFileName = String.format("%s-%s", jsonFilePrefix, autoIndex);
            File newFile = new File(targetDir, newFileName + ".json");
            //因为内存原因，所以只能通过拼凑的形式做成json形式,拼凑的内容先写在内存中
            StringBuilder stringBuilder = new StringBuilder();
            //首先拼接{括号
            stringBuilder.append(String.format("{%n"));
            //获取文件迭代器,方便读取文件
            LineIterator fileContents = FileUtils.lineIterator(readFile, "UTF-8");
            //读取文件
            for (int i = 0; i < realRealLine; i++) {
                //取余，用于判断是否是新数组的开始， 或者是否需要拼接逗号
                int remainder = i % arraysSize;
                //数组名的索引,json数组的索引名，是否需要新的数组名
                int tempJsonArrayKeyIndex = i / arraysSize;
                //是否是新数组
                if (remainder == 0 && tempJsonArrayKeyIndex < jsonArrayKeyArrays.length) {
                    //第一数组 和 非第一个数组
                    if (tempJsonArrayKeyIndex == 0)
                        stringBuilder.append(String.format("\"%s\":[%n", jsonArrayKeyArrays[tempJsonArrayKeyIndex]));
                    else
                        stringBuilder.append(String.format("],%n\"%s\":[%n", jsonArrayKeyArrays[tempJsonArrayKeyIndex]));
                }
                //获取文件并且切割
                String next = fileContents.next();
                String[] split = StringUtils.split(next, delimiter);
                JsonObject jsonObject = new JsonObject();
                //生成json对象
                for (int j = 0; j < split.length; j++)
                    jsonObject.addProperty(ArrayUtils.isEmpty(jsonObjectKeyArrays) ? String.format("key-%s", j) : jsonObjectKeyArrays[j], split[j]);
                //判断是否要拼接逗号,最后一行不需要逗号
                if (remainder == (arraysSize - 1) && tempJsonArrayKeyIndex < jsonArrayKeyArrays.length - 1)
                    stringBuilder.append(String.format("%s%n", jsonObject.toString()));
                else
                    stringBuilder.append(String.format("%s,%n", jsonObject.toString()));
                //如果超过 2000行数据，就需要写入文件中, 最后几行文件一定要放在最后
                if (i % 2000 == 0 && (realRealLine - i) > 2000) {
                    FileUtils.writeStringToFile(newFile, stringBuilder.toString(), Charset.forName("utf-8"), true);
                    stringBuilder = new StringBuilder();
                }
            }
            //处理数组结尾,等问题
            stringBuilder.delete(stringBuilder.lastIndexOf(","), stringBuilder.length());
            stringBuilder.append(String.format("%n]%n}"));
            FileUtils.writeStringToFile(newFile, stringBuilder.toString(), Charset.forName("utf-8"), true);
            System.out.println(String.format("%s is created", newFile.getPath()));
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (countDownLatch != null)
                countDownLatch.countDown();
        }
        return null;
    }
}
