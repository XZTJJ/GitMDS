package com.zhouhc.endpointer.utils;

//某些常量的状态
public class ConstantUtil {
    //数据状态,责任链状态的保存
    public static final String PPSTATUS = "ppstate";
    //真是数据保存
    public static final String DATAPREFIX = "value-";
    //记录本机的ip地址,分别在启动的时候监听
    public volatile static String HostAddr;
    public volatile static int Post;

    //websocket的相关配置
    //端口
    public static final String WEBSOCKETENDPOINTER = "/streamData";
    //消息代理前缀
    public static final String WEBSOCKETMESSAGEPREFIX = "/topic";
    //客户端消息订阅前缀
    public static final String WEBSOCKETDESTIONPREFIX = "/app";
    //默认的订阅地址
    public static final String WEBSOCKETCLIENTSCR = "/stream";
}
