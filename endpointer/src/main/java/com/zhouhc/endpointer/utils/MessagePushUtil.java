package com.zhouhc.endpointer.utils;

import com.zhouhc.endpointer.socket.DataPushSocket;

//数据推送工具类
public class MessagePushUtil {

    //数据推送
    public static void pushDataToRemote(String streamName, String message) {
        getDataPushSocket().sendMessage(streamName, message);
    }


    //获取websocket的链接类
    private static DataPushSocket getDataPushSocket() {
        return SpringContextUtil.getBean(DataPushSocket.class);
    }
}
