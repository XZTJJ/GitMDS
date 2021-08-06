package com.zhouhc.endpointer.controller;

import com.zhouhc.endpointer.compent.RedisInsertTask;
import com.zhouhc.endpointer.redisSub.KeyExpiredLinster;
import com.zhouhc.endpointer.redisSub.PushChannelLinster;
import com.zhouhc.endpointer.thread.CThreadFactory;
import com.zhouhc.endpointer.utils.GetCofUtil;
import com.zhouhc.endpointer.utils.MsgUtils;
import com.zhouhc.endpointer.utils.RedisUtil;
import com.zhouhc.endpointer.utils.SpringContextUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.Topic;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;


//redis队列测试
@RestController
@RequestMapping("/redis")
public class RedisInsertTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(RedisInsertTest.class);


    @PostMapping("/intsert")
    public String insertData() {
        ExecutorService executors = null;
        try {
            final String[] streamNames = StringUtils.split(GetCofUtil.getValue("custom.redis-task.name", ""), ",");
            final AtomicInteger[] streamCounts = Arrays.stream(StringUtils.split(GetCofUtil.getValue("custom.redis-task.count", ""), ","))
                    .map(intStr -> new AtomicInteger(Integer.valueOf(intStr))).toArray(AtomicInteger[]::new);
            if (streamNames.length != streamCounts.length || streamCounts.length == 0)
                throw new RuntimeException("stream不匹配，或者为0");
            //开始创建
            int[] size = Arrays.stream(streamCounts).mapToInt(streamCount -> streamCount.get()).toArray();
            CountDownLatch countDownLatch = new CountDownLatch((int) Arrays.stream(size).sum());
            //线程数量
            int threadSize = GetCofUtil.getValue("custom.redis-task.threadSize", Integer.class, 5);
            executors = Executors.newFixedThreadPool(threadSize, new CThreadFactory("redis-insert"));
            for (int i = 0; i < streamNames.length; i++) {
                for (int j = 0; j < size[i]; j++)
                    executors.submit(new RedisInsertTask(streamNames[i], countDownLatch, streamCounts[i]));
            }
            countDownLatch.await();
        } catch (Exception e) {
            LOGGER.error("插入出现异常", e);
            return MsgUtils.ErrorMsg();
        } finally {
            if (executors != null)
                executors.shutdown();
        }
        return MsgUtils.successMsg();
    }


    @GetMapping("/zsorce")
    public String zsorce() {
        try {
            ZSetOperations<String, String> stringStringZSetOperations = SpringContextUtil.getBean(StringRedisTemplate.class).opsForZSet();
            for (int i = 0; i < 10; i++) {
                LocalDateTime now = LocalDateTime.now().minusYears(i);
                long epochMilli = now.toInstant(ZoneOffset.of("+8")).toEpochMilli();
                stringStringZSetOperations.add("times", i + "-", (double) epochMilli);
                stringStringZSetOperations.add("times", i + "", (double) epochMilli + 1);
            }
        } catch (Exception e) {
            LOGGER.error("error", e);
        }
        return MsgUtils.successMsg();
    }


    @GetMapping("/gsorce")
    public String gsorce() {
        try {
            ZSetOperations<String, String> stringStringZSetOperations = SpringContextUtil.getBean(StringRedisTemplate.class).opsForZSet();
            Set<String> times = stringStringZSetOperations.rangeByScore("times", 1533686888000L, 1625702888000L);
            Map<String, String> hashkey = RedisUtil.hScan("hashkey", "*", 1, String.class, String.class);
            Set<Integer> scan = RedisUtil.scan("*", 100, Integer.class);
            System.out.println("");
        } catch (Exception e) {
            LOGGER.error("error", e);
        }
        return MsgUtils.successMsg();
    }

    @GetMapping("/subChannel")
    public String subChannel() {
        try {
            //key过期订阅
            KeyExpiredLinster keyExpiredLinster = new KeyExpiredLinster();
            Topic keyExTopic = new PatternTopic("__keyevent@0__:expired");
            RedisUtil.subscribe(keyExpiredLinster,keyExTopic);

            PushChannelLinster pushChannelLinster = new PushChannelLinster();
            Topic channelTopic = new ChannelTopic("zhouhc");
            RedisUtil.subscribe(pushChannelLinster,channelTopic);

        } catch (Exception e) {
            LOGGER.error("error", e);
        }
        return MsgUtils.successMsg();
    }

}
