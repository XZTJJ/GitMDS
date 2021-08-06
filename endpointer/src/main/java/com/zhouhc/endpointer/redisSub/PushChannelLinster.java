package com.zhouhc.endpointer.redisSub;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;


//redis的健过期事件监听
public class PushChannelLinster implements MessageListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(PushChannelLinster.class);

    @Override
    public void onMessage(Message message, byte[] pattern) {
        try {
            LOGGER.info(message.toString());
            LOGGER.info(new String(message.getBody(), "utf-8"));
            LOGGER.info(new String(message.getChannel(), "utf-8"));
        }catch (Exception e){
            LOGGER.error("",e);
        }
    }
}
