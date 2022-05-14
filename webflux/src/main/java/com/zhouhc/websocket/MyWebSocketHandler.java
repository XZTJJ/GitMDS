package com.zhouhc.websocket;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.springframework.stereotype.Component;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsConfigurationSource;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.WebSocketSession;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/*
 * webSocket适配，用于来处连接流的 WebSocketHandler对返回格式有要求
 */
public class MyWebSocketHandler implements WebSocketHandler {

    @Override
    public Mono<Void> handle(WebSocketSession session) {
        String path = session.getHandshakeInfo().getUri().getPath();
        System.out.println("请求路径为: " + path);
        //输入流
        Mono<Void> input = session.receive().map(WebSocketMessage::getPayloadAsText)
                .flatMap(message -> {
                    WebsocketOperate.putWebSocketSession(StringUtils.substringAfterLast(path,"/"), session);
                    return Mono.empty();
                }).then();
        //输出流
        Mono<Void> output = session.send(Flux.create(
                sink -> WebsocketOperate.putWebSocketSession(session, new MyWebSocketContextHolder(session, sink))
        ));
        //整合完成
        return Mono.zip(input, output).then(Mono.fromRunnable(() -> {
            WebsocketOperate.removeWebSocketSession(session);
        }));
//        return session
//                .receive()
//                .map(WebSocketMessage::getPayloadAsText)
//                .map(tm -> "Echo: " + tm)
//                .map(session::textMessage)
//                .as(session::send);

    }

}
