package com.zhouhc.websocket;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.FluxSink;

/**
 * 简单的保存websocket,在其他地方发送,
 * 非常简单的方法,仅仅保存seesion 和 Publisher,
 * 仅仅提供一个方法而已
 */
@AllArgsConstructor
@Getter
public class MyWebSocketContextHolder {

    private final WebSocketSession session;
    private final FluxSink<WebSocketMessage> sink;

    public void sendData(String message) {
        sink.next(session.textMessage(message));
    }
}
