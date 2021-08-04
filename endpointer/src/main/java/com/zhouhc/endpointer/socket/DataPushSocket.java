package com.zhouhc.endpointer.socket;

import com.google.gson.JsonObject;
import com.zhouhc.endpointer.utils.ConstantUtil;
import com.zhouhc.endpointer.utils.JSONUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Controller;

//数据推送的websocket
@Controller
public class DataPushSocket {

    private static final Logger LOGGER = LoggerFactory.getLogger(DataPushSocket.class);


    /**
     * 消息发送工具对象
     */
    @Autowired
    private SimpMessageSendingOperations simpMessageSendingOperations;

    /**
     * 将消息发送到指定的目标地址,一般而言 content 包含了 ，要发送消息的地址，
     */
    @MessageMapping("/client")
    public void sendTopicMessage(String content) {
        // 将消息发送到 WebSocket 配置类中配置的代理中（/topic）进行消息转发
        JsonObject requestJson = JSONUtil.toJson(content);
        LOGGER.info("服务端接受到的消息为 : {}", content);
        final String prefix = "服务器消息简单回现: ";
        String destination = JSONUtil.toString(requestJson, "destination");
        String message = StringUtils.replace(JSONUtil.toString(requestJson, "content"), prefix, "");
        simpMessageSendingOperations.convertAndSend(destination, JSONUtil.getJsonFromArrays("data", prefix + message).toString());
    }

    //streamName暂时用不到，没有为什么
    public void sendMessage(String streamName, String content) {
        // 将消息发送到 WebSocket 配置类中配置的代理中（/topic）进行消息转发
        simpMessageSendingOperations.convertAndSend(ConstantUtil.WEBSOCKETMESSAGEPREFIX + ConstantUtil.WEBSOCKETCLIENTSCR, content);
    }

}
