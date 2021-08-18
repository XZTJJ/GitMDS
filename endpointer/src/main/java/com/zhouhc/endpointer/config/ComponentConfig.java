package com.zhouhc.endpointer.config;

import com.google.gson.Gson;
import com.zhouhc.endpointer.utils.GetCofUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.http.converter.json.GsonHttpMessageConverter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;


@Configuration
public class ComponentConfig {


    //创建redis链接工厂
    @Bean
    public LettuceConnectionFactory redisConnectionFactory() {
        //redis的相关配置信息
        RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration();
        redisStandaloneConfiguration.setHostName(GetCofUtil.getValue("custom.redis.host", String.class));
        redisStandaloneConfiguration.setPort(GetCofUtil.getValue("custom.redis.port", Integer.class));
        redisStandaloneConfiguration.setPassword(GetCofUtil.getValue("custom.redis.password", ""));
        redisStandaloneConfiguration.setDatabase(GetCofUtil.getValue("custom.redis.database", Integer.class, 0));
        return new LettuceConnectionFactory(redisStandaloneConfiguration);
    }

    //redis的监听容器,
    @Bean
    public RedisMessageListenerContainer redisMessageListenerContainer(RedisConnectionFactory redisConnectionFactory) {
        RedisMessageListenerContainer redisMessageListenerContainer = new RedisMessageListenerContainer();
        redisMessageListenerContainer.setConnectionFactory(redisConnectionFactory);
        return redisMessageListenerContainer;
    }

    //处理跨域问题
    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration config = new CorsConfiguration();
        config.addAllowedOriginPattern("*");
        config.setAllowCredentials(true);
        config.addAllowedMethod("*");
        config.addAllowedHeader("*");
        config.addExposedHeader("*");
        UrlBasedCorsConfigurationSource configSource = new UrlBasedCorsConfigurationSource();
        configSource.registerCorsConfiguration("/**", config);

        return new CorsFilter(configSource);
    }

    //使用Gson序列话
    @Bean
    public GsonHttpMessageConverter customGsonHttpMessageConverter(Gson gson) {
        return new GsonHttpMessageConverter(gson);
    }
}
