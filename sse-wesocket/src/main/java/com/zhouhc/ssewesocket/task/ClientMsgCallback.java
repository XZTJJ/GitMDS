package com.zhouhc.ssewesocket.task;


import com.google.common.util.concurrent.FutureCallback;
import com.google.gson.JsonObject;
import com.zhouhc.ssewesocket.cInt.SendMSInt;
import com.zhouhc.ssewesocket.compent.ClientPipeline;
import com.zhouhc.ssewesocket.thread.GlobalThreadPool;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;

/**
 * 为每个客户端组装信息，并且将每个客户端的发送任务提交到线程池
 * 和 RedisSubTask 配合使用
 */
public class ClientMsgCallback implements FutureCallback<Map<String, String>> {
    //日志类
    private final static Logger LOGGER = LoggerFactory.getLogger(ClientMsgCallback.class);

    //处理成功的方法
    @Override
    public void onSuccess(@Nullable Map<String, String> keyValueMessage) {
        try {
            if (keyValueMessage == null || keyValueMessage.size() == 0)
                return;
            //获取任务
            for (Map.Entry<String, String> tempMap : keyValueMessage.entrySet()) {
                final String channelName = tempMap.getKey();
                final String message = tempMap.getValue();
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty(channelName, message);
                //获取所有的客户端
                Set<SendMSInt> clients = ClientPipeline.getInstance().getClients(channelName);
                //发送消息不需要监听对应的结果，不管成功和失败都可以不用管
                clients.stream().forEach(snedMsInt -> GlobalThreadPool.exec(new PushTask(snedMsInt, jsonObject.toString())));
            }
        }catch (Exception e){
            LOGGER.error("ClientMsg回调出现错误",e);
        }
    }

    @Override
    public void onFailure(Throwable t) {
        LOGGER.error("ClientMsgCallback异步回掉错误", t);
    }
}
