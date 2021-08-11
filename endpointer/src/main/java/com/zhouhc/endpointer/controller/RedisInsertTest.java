package com.zhouhc.endpointer.controller;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.reflect.TypeToken;
import com.zhouhc.endpointer.compent.RedisInsertTask;
import com.zhouhc.endpointer.error.CustomException;
import com.zhouhc.endpointer.redisSub.KeyExpiredLinster;
import com.zhouhc.endpointer.redisSub.PushChannelLinster;
import com.zhouhc.endpointer.thread.CThreadFactory;
import com.zhouhc.endpointer.utils.*;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.Topic;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.nio.charset.Charset;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.zhouhc.endpointer.renum.ErrorEnum.redisCursorError;


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
            LocalDateTime localDateTime = LocalDateTime.now();
            for (int i = 0; i < 10; i++) {
                LocalDateTime now = localDateTime.minusYears(i);
//                long epochMilli = now.toInstant(ZoneOffset.of("+8")).toEpochMilli();
                long epochMilli = now.toEpochSecond(ZoneOffset.of("+8"));
                System.out.printf("现在存入的时间为: %s%n", epochMilli);
                stringStringZSetOperations.add("flow:stream1", "stream1:yyyyMMddHHmmss:" + i, (double) epochMilli);
                for (int j = 0; j < 100; j++)
                    RedisUtil.hSet("stream1:yyyyMMddHHmmss:" + i, "key" + j, "value" + j);
                stringStringZSetOperations.add("flow:stream1", "stream1:yyyyMMddHHmmss:0" + i, (double) epochMilli + 1);
                for (int j = 0; j < 100; j++)
                    RedisUtil.hSet("stream1:yyyyMMddHHmmss:0" + i, "key" + j, "value" + j);
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
            Set<String> times = stringStringZSetOperations.rangeByScore("flow:stream1", 0L, LocalDateTime.now().toEpochSecond(ZoneOffset.of("+8")));
            Set<String> times_back = RedisUtil.zRangeByScore("flow:stream1", 0L, LocalDateTime.now().toEpochSecond(ZoneOffset.of("+8")));
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
            RedisUtil.subscribe(keyExpiredLinster, keyExTopic);

            PushChannelLinster pushChannelLinster = new PushChannelLinster();
            Topic channelTopic = new ChannelTopic("zhouhc");
            RedisUtil.subscribe(pushChannelLinster, channelTopic);

        } catch (Exception e) {
            LOGGER.error("error", e);
        }
        return MsgUtils.successMsg();
    }

    @GetMapping("hset")
    public String hset() {
        Map<Integer, Integer> hashKey = RedisUtil.hScan("hashKey", "*", 100, Integer.class, Integer.class);
        System.out.println();


        ScanOptions build = ScanOptions.scanOptions().match("flow:*").count(1000).build();
        RedisConnection connection = SpringContextUtil.getBean(StringRedisTemplate.class).getConnectionFactory().getConnection();
        Cursor<byte[]> scan = connection.scan(build);

        Set<String> collect = null;
        try (Cursor<byte[]> cursor = connection.scan(build)) {
            collect = cursor.stream().map(byteArrays -> JSONUtil.toT(new String(byteArrays, Charset.forName("utf-8")), String.class)).collect(Collectors.toSet());
        } catch (Exception e) {
            throw new CustomException(redisCursorError, e);
        }
        return MsgUtils.successMsg();
    }

    @GetMapping("/toT")
    public String toT() {
        List<String> collect = Stream.<String>of("1 2 3 4 5 6").map(str -> str.split(" ")).filter(arrays -> arrays != null && arrays.length > 0).flatMap(arrays -> Arrays.stream(arrays)).collect(Collectors.toList());
        collect.subList(0, 2);

        List<?> test = new ArrayList();
        test.add(null);

        return MsgUtils.successMsg();
    }

}
