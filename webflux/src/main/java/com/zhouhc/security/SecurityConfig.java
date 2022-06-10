package com.zhouhc.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import reactor.core.publisher.Mono;

/**
 *  配置对应的 webflux 的 spring security 设置
 *
 * 认证: AuthenticationManager认证的，调用authenticate()方法。webflux中可以自定义 ReactiveAuthenticationManager。
 * 鉴权: 可以自定义,比如直接写hasrole()之类的，也可以自定义，自定义看下面:
 * 连接认证和鉴权的只有一个 UserDetails(spring security自带的)。这个就包含了身份信息和认证信息了。
 * 具体的判断是 ReactiveAuthorizationManager 的 check方法 (其实Authentication 的 isAuthenticated() 是否为true)
 */
@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
public class SecurityConfig {
    @Autowired
    private MyAuthenticationManager authenticationManager;

    @Value("${security.websocket-uri}")
    private String websocketUri;

    @Autowired
    private MyServerSecurityContextRepository securityContextRepository;


    @Bean
    public SecurityWebFilterChain webFluxSecurityFilterChain(ServerHttpSecurity http) throws Exception {

        return http
                .exceptionHandling()
                .authenticationEntryPoint(
                        (swe, e) -> {
                            return Mono.fromRunnable(() -> swe.getResponse().setStatusCode(HttpStatus.NETWORK_AUTHENTICATION_REQUIRED));
                        }
                ).accessDeniedHandler(
                        (swe, e) -> {
                            return Mono.fromRunnable(() -> swe.getResponse().setStatusCode(HttpStatus.NOT_EXTENDED));
                        }
                ).and()
                .csrf().disable()
                .formLogin().disable()
                .httpBasic().disable()
                //设置认证类
                .authenticationManager(authenticationManager)
                //设置上下文只有类
                .securityContextRepository(securityContextRepository)
                .authorizeExchange()
                .pathMatchers(HttpMethod.OPTIONS).permitAll()
                //websocket的必须为 admin 才让看
                .pathMatchers(websocketUri).hasRole("ADMIN")
                //这里可以设置无需 鉴权的 地址
                //.pathMatchers(excludedAuthPages).permitAll()
                .anyExchange().authenticated()
                .and().build();
    }

}
