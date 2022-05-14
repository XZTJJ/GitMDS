package com.zhouhc.util;

import com.zhouhc.security.MyUserDetails;
import com.zhouhc.websocket.WebsocketOperate;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClientRequest;

import java.time.Duration;


@Component
public class TimedTask {

    private final WebClient webClient = WebClient.create();

    @Value("${security.auth-server}")
    private String authServer;

    @Scheduled(fixedDelayString = "5000", initialDelay = 60000)
    public void timetaks() {
        if (!WebsocketOperate.hasWebSocket())
            return;
        String url = authServer + (StringUtils.endsWith(authServer, "/") ? "api/login/user" : "/api/login/user");
        MyUserDetails myUserDetails = webClient.get().uri(url).header(MyConstant.ACCESS_TOKEN, "3")
                .httpRequest(httpRequest -> {
                    HttpClientRequest reactorRequest = httpRequest.getNativeRequest();
                    reactorRequest.responseTimeout(Duration.ofSeconds(15));
                })
                .exchangeToMono(response -> {
                    if (response.headers().header("Content-Type").stream().noneMatch(contentTyep -> StringUtils.containsIgnoreCase(contentTyep, "application/json")))
                        return Mono.error(new RuntimeException("the response Content-Type header is not json, the response header must be json, please check the login api interface"));
                    else if (response.statusCode().is2xxSuccessful())
                        return response.bodyToMono(MyUserDetails.class);
                    else
                        return response.createException().flatMap(Mono::error);
                }).block();

        WebsocketOperate.getWebSocket().forEach(websocket -> websocket.sendData(myUserDetails.toString()));
    }

}
