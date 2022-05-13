package com.zhouhc.security;

import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

/*
 * 其实是在这里做的认证和鉴权,在  ServerSecurityContextRepository 调用后会回掉方法,
 * 因为 ServerSecurityContextRepository 中的 UsernamePasswordAuthenticationToken 默认
 * 是  isAuthenticated() 为true的，所以拥有所有的权限
 */
@Component
public class MyAuthenticationManager implements ReactiveAuthenticationManager {
    @Override
    public Mono<Authentication> authenticate(Authentication authentication) {
        try {
            UsernamePasswordAuthenticationToken atoken = (UsernamePasswordAuthenticationToken) authentication.getPrincipal();
            if (atoken == null)
                return Mono.empty();
            return Mono.just(atoken);
        } catch (Exception e) {
            return Mono.empty();
        }
    }
}
