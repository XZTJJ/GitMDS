package com.zhouhc.task;

import com.google.gson.JsonObject;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.nio.charset.Charset;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;

//文件转换类
public class JsonFilesTask implements Callable<Boolean> {
    private final String content;
    private final String targetDir;
    private final String Delimiter;
    private final String jsonKey;
    private final String jsonFilePrefix;
    private final int autoIndex;
    private final CountDownLatch countDownLatch;

    //某些变量的初始化
    public JsonFilesTask(String content, String targetDir, String delimiter, String jsonKey, String jsonFilePrefix, int autoIndex, CountDownLatch countDownLatch) {
        this.content = content;
        this.targetDir = targetDir;
        Delimiter = delimiter;
        this.jsonKey = jsonKey;
        this.jsonFilePrefix = jsonFilePrefix;
        this.autoIndex = autoIndex;
        this.countDownLatch = countDownLatch;
    }

    @Override
    public Boolean call() throws Exception {
        try {
            String[] conArrays = StringUtils.split(content, Delimiter);
            if (conArrays == null || conArrays.length < 1)
                return true;
            JsonObject conJson = new JsonObject();
            //json的 key 值
            String[] jsonKeys = StringUtils.split(jsonKey, ",");
            for (int i = 0; i < conArrays.length; i++)
                conJson.addProperty((jsonKeys == null || jsonKeys.length < 1) ? String.format("key-%s", (i + 1)) : jsonKeys[i], conArrays[i]);
            FileUtils.writeStringToFile(new File(targetDir, String.format("%s-%s.json", jsonFilePrefix, autoIndex)), conJson.toString(), Charset.forName("utf-8"), false);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            if (countDownLatch != null) {
                countDownLatch.countDown();
                long count = countDownLatch.getCount();
                if (count % 500 == 0)
                    System.out.println(String.format("left %s lines to handler...", count));
            }

        }
    }
}
