package com.zhouhc.security;

import com.zhouhc.util.MyConstant;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.web.server.context.ServerSecurityContextRepository;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClientRequest;

import java.time.Duration;


/**
 *  这里进行token的解析，并且保存到 spring security context 中,
 *  其实一般这里就是查数据库，查看是否存在改用户，不过，应用需要
 *  请求 其他认证服务器 ，不需要查询数据库了
 */
@Component
public class MyServerSecurityContextRepository implements ServerSecurityContextRepository {
    //认证服务器地址
    @Value("${security.auth-server}")
    private String authServer;

    private final WebClient webClient = WebClient.create();

    @Override
    public Mono<Void> save(ServerWebExchange exchange, SecurityContext context) {
        throw new UnsupportedOperationException("can not save user information");
    }

    @Override
    public Mono<SecurityContext> load(ServerWebExchange exchange) {
        ServerHttpRequest request = exchange.getRequest();
        String acctoken = request.getHeaders().getFirst(MyConstant.ACCESS_TOKEN);
        String url = authServer + (StringUtils.endsWith(authServer, "/") ? "api/login/user" : "/api/login/user");
        if (StringUtils.isBlank(acctoken) || StringUtils.isBlank(url))
            return Mono.empty();
        return webClient.get().uri(url).header(MyConstant.ACCESS_TOKEN, acctoken)
                .httpRequest(httpRequest -> {
                    HttpClientRequest reactorRequest = httpRequest.getNativeRequest();
                    reactorRequest.responseTimeout(Duration.ofSeconds(15));
                })
                .exchangeToMono(response -> {
                    if (response.statusCode().is2xxSuccessful())
                        return response.bodyToMono(MyUserDetails.class);
                    else
                        return response.createException().map(e -> Mono.error(e));
                }).flatMap(userDetails -> {
                    if (userDetails == null)
                        return Mono.empty();
                    else if (Exception.class.isAssignableFrom(userDetails.getClass()))
                        return Mono.error((Exception) userDetails);
                    else
                        return Mono.defer(() -> Mono.just(new SecurityContextImpl(new UsernamePasswordAuthenticationToken(userDetails, userDetails, ((MyUserDetails) userDetails).getAuthorities()))));
                });
    }
}
