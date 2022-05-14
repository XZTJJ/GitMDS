package com.zhouhc.websocket;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.HandlerAdapter;
import org.springframework.web.reactive.HandlerMapping;
import org.springframework.web.reactive.handler.SimpleUrlHandlerMapping;
import org.springframework.web.reactive.socket.server.support.WebSocketHandlerAdapter;

import java.util.Collections;

@Configuration
public class WebSocketConfig {

    @Value("${security.websocket-uri}")
    private String websocketUri;

    @Bean
    public HandlerMapping handlerMapping() {
        SimpleUrlHandlerMapping mapping = new SimpleUrlHandlerMapping();
        mapping.setUrlMap(Collections.singletonMap(websocketUri, new MyWebSocketHandler()));
        mapping.setOrder(-1);
        return mapping;
    }

    @Bean
    public HandlerAdapter handlerAdapter() {
        return new WebSocketHandlerAdapter();
    }
}
