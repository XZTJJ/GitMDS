package com.zhouhc.ssewesocket.task;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

/**
 * redis的订阅任务, 每次获取消息都应该是一个
 * 异步任务，防止因为网络或者其他耗时的情况.
 * <p>
 * 返回 key-value的形式。 key为redis的key，value为值得Json字符串形式
 */
public class RedisSubTask implements Callable<Map<String, String>> {

    private static final Logger LOGGER = LoggerFactory.getLogger(RedisSubTask.class);
    //订阅的消息
    private String subKeyMessage;

    public RedisSubTask(String subKeyMessage) {
        this.subKeyMessage = subKeyMessage;
    }

    //根据消息处理，key分解，获取，转成jsonStr,key为index , vlaue为index+1
    @Override
    public Map<String, String> call() throws Exception {
        try {
            //
            TimeUnit.SECONDS.sleep(2);
            Map<String, String> map = new Gson().fromJson(subKeyMessage, new TypeToken<HashMap<String, String>>() {
            }.getType());
            return map;
        } catch (Exception e) {
            LOGGER.error("订阅任务出现错误", e);
            return null;
        }
    }
}
