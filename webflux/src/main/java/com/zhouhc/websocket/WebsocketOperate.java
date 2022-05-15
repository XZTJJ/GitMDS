package com.zhouhc.websocket;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.reactive.socket.WebSocketSession;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 统一的处理 元数据 的中心，仅仅只是简单的操作。
 * 只是保存保存一些数据和指标
 *
 */
public class WebsocketOperate {
    private final static Logger LOGGER = LoggerFactory.getLogger(WebsocketOperate.class);
    private final static Map<Object, Object> metaData = new HashMap<Object, Object>();

    private final static String SEESION_APPID = "said";
    private final static String SEESION_SOCKETCONTEXTHODER = "ssch";

    private final static String ALL_APPID_METRIC = "all_metric";

    /* 原来的实现方式，现在改了
    //保存会话的，被废弃了
    public static synchronized void putWebSocketSession(String appId, WebSocketSession session) {
        metaData.putIfAbsent(appId, session);
        metaData.putIfAbsent(session, new HashMap<String, Object>());
        ((HashMap<String, Object>) metaData.get(session)).putIfAbsent(SEESION_APPID, appId);
        LOGGER.info("添加{}成功", appId);
    }

    //保存会话的，被废弃了
    public static synchronized void putWebSocketSession(WebSocketSession session, MyWebSocketContextHolder socketContextHolder) {
        metaData.putIfAbsent(session, new HashMap<String, Object>());
        ((HashMap<String, Object>) metaData.get(session)).putIfAbsent(SEESION_SOCKETCONTEXTHODER, socketContextHolder);
        LOGGER.info("添加seesion: {}成功", session.toString());
    }

    //移除会话的，被废弃了
    public static synchronized void removeWebSocketSession(WebSocketSession session) {
        HashMap<String, Object> sessionMap = (HashMap<String, Object>) metaData.remove(session);
        if (sessionMap != null && sessionMap.containsKey(SEESION_APPID))
            metaData.remove(sessionMap.get(SEESION_APPID));
        LOGGER.info("移除seesion: {}成功, 当前元数据大小:{} ", session.toString(), metaData.size());
    }*/

    //保存会话的
    public static synchronized void putWebSocketSession(String appId, MyWebSocketContextHolder socketContextHolder) {
        if (StringUtils.isBlank(appId) || socketContextHolder == null)
            return;
        metaData.putIfAbsent(appId, socketContextHolder);
        LOGGER.info("添加appid为{}成功,socketContextHolder为{}, seesion为{}", appId, socketContextHolder, socketContextHolder.getSession());
    }

    //保存指标的
    public static synchronized void putWebSocketMetrics(String appid, String metrics) {
        if (StringUtils.isBlank(appid) || StringUtils.isBlank(metrics))
            return;
        LOGGER.info("添加id是{}的{}指标", appid, metrics);
    }

    //移除会话
    public static synchronized void removeWebSocketSession(String appid) {
        if (StringUtils.isBlank(appid))
            return;
        Object session = metaData.remove(appid);
        LOGGER.info("metaData 移除了 id为 {},socketContextHolder为{}", appid, session);
        LOGGER.info("剩余当前元数据大小:{} ", metaData.size());
    }

    //判断是否有会话
    public static synchronized boolean hasWebSocket() {
        return metaData.values().stream().anyMatch(value -> MyWebSocketContextHolder.class.isAssignableFrom(value.getClass()));
    }

    //判断有哪些values
    public static synchronized Set<MyWebSocketContextHolder> getWebSocket() {
        Set<MyWebSocketContextHolder> collect = metaData.values().stream().filter(value -> MyWebSocketContextHolder.class.isAssignableFrom(value.getClass())).map(
                value -> (MyWebSocketContextHolder) value
        ).collect(Collectors.toSet());
        return Collections.unmodifiableSet(collect);
    }
}
