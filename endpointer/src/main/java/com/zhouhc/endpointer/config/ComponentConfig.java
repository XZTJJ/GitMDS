package com.zhouhc.endpointer.config;

import com.zhouhc.endpointer.utils.GetCofUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;


@Configuration
public class ComponentConfig {


    //创建redis链接工厂
    @Bean
    public LettuceConnectionFactory redisConnectionFactory() {
        return new LettuceConnectionFactory(new RedisStandaloneConfiguration(
                GetCofUtil.getValue("custom.redis.host", String.class), GetCofUtil.getValue("custom.redis.port", Integer.class)));
    }

}
