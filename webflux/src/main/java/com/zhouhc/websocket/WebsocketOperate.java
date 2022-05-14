package com.zhouhc.websocket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.reactive.socket.WebSocketSession;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
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


    public static synchronized void putWebSocketSession(String appId, WebSocketSession session) {
        metaData.putIfAbsent(appId, session);
        metaData.putIfAbsent(session, new HashMap<String, Object>());
        ((HashMap<String, Object>) metaData.get(session)).putIfAbsent(SEESION_APPID, appId);
        LOGGER.info("添加{}成功", appId);
    }

    public static synchronized void putWebSocketSession(WebSocketSession session, MyWebSocketContextHolder socketContextHolder) {
        metaData.putIfAbsent(session, new HashMap<String, Object>());
        ((HashMap<String, Object>) metaData.get(session)).putIfAbsent(SEESION_SOCKETCONTEXTHODER, socketContextHolder);
        LOGGER.info("添加seesion: {}成功", session.toString());
    }

    public static synchronized void removeWebSocketSession(WebSocketSession session) {
        HashMap<String, Object> sessionMap = (HashMap<String, Object>) metaData.remove(session);
        if (sessionMap != null && sessionMap.containsKey(SEESION_APPID))
            metaData.remove(sessionMap.get(SEESION_APPID));
        LOGGER.info("移除seesion: {}成功, 当前元数据大小: ", session.toString(), metaData.size());
    }


    public static synchronized boolean hasWebSocket() {
        return metaData.keySet().stream().anyMatch(key -> WebSocketSession.class.isAssignableFrom(key.getClass()));
    }

    public static synchronized Set<MyWebSocketContextHolder> getWebSocket() {
        Set<MyWebSocketContextHolder> collect = metaData.keySet().stream().filter(key -> WebSocketSession.class.isAssignableFrom(key.getClass()))
                .map(key -> (MyWebSocketContextHolder) ((HashMap<String, Object>) metaData.get(key)).get(SEESION_SOCKETCONTEXTHODER))
                .collect(Collectors.toSet());
        return Collections.unmodifiableSet(collect);
    }
}
