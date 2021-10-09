package com.zhouhc.endpointer.controller;

import com.zhouhc.endpointer.compent.RedisInsertTask;
import com.zhouhc.endpointer.error.CustomException;
import com.zhouhc.endpointer.redisSub.KeyExpiredLinster;
import com.zhouhc.endpointer.redisSub.PushChannelLinster;
import com.zhouhc.endpointer.thread.CThreadFactory;
import com.zhouhc.endpointer.utils.*;
import org.apache.commons.lang3.StringUtils;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
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

import java.net.URI;
import java.nio.charset.Charset;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;
import java.util.concurrent.*;
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
            final String[] streamNames = StringUtils.split(GetCofUtil.getValue("custom.redis-task.name", "stream,stream,stream"), ",");
            final AtomicInteger[] streamCounts = Arrays.stream(StringUtils.split(GetCofUtil.getValue("custom.redis-task.count", "4000000,4000000,4000000"), ","))
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
            for (int i = 0; i < 50; i++) {
                LocalDateTime now = localDateTime.minusDays(i);
//                long epochMilli = now.toInstant(ZoneOffset.of("+8")).toEpochMilli();
                long epochMilli = now.toEpochSecond(ZoneOffset.of("+8"));
                System.out.printf("现在存入的时间为: %s%n", epochMilli);
                stringStringZSetOperations.add("redis:redis", String.format("redis:yyyyMMddHHmmss:%s", i), (double) epochMilli);
                for (int j = 0; j < 20; j++) {
                    RedisUtil.hSet(String.format("redis:yyyyMMddHHmmss:%s", i), String.format("key%s-%s",i, j), String.format("value%s-%s", i, j));
                    RedisUtil.expire(String.format("redis:yyyyMMddHHmmss:%s", i), ThreadLocalRandom.current().nextInt(30) + 3600 * 3);
                }
//                stringStringZSetOperations.add("flow:stream1", String.format("stream1:yyyyMMddHHmmss:0-%s",i), (double) epochMilli + 1);
//                for (int j = 0; j < 20; j++) {
//                    RedisUtil.hSet(String.format("stream1:yyyyMMddHHmmss:0-%s",i), String.format("key-%s",i,j), String.format("value0-%s-%s",i,j));
//                }
            }
            RedisUtil.set("redis", 3600 * 3);
        } catch (Exception e) {
            LOGGER.error("error", e);
        }
        return MsgUtils.successMsg();
    }


    @GetMapping("/gsorce")
    public String gsorce() {
        try {
            ZSetOperations<String, String> stringStringZSetOperations = SpringContextUtil.getBean(StringRedisTemplate.class).opsForZSet();
            Set<String> times = stringStringZSetOperations.rangeByScore("redis:stream1", 0L, LocalDateTime.now().toEpochSecond(ZoneOffset.of("+8")));
            Set<String> times_back = RedisUtil.zRangeByScore("redis:stream1", 0L, LocalDateTime.now().toEpochSecond(ZoneOffset.of("+8")));
            Map<String, String> hashkey = RedisUtil.hScan("hashkey", "*", 1, String.class, String.class);
            Set<Integer> scan = RedisUtil.scan("*", 100, Integer.class);
            System.out.println("");
        } catch (Exception e) {
            LOGGER.error("error", e);
        }
        return MsgUtils.successMsg();
    }

    @GetMapping("/publish")
    public String publish() {
        try {
            int count = 0;
            while (count < 2000) {
                String key = String.format("sst:YYYYDDMM:%s", count);
                for (int j = 0; j < 20; j++)
                    RedisUtil.hSet(key, String.format("key-%s", j), String.format("value-%s", j));
                RedisUtil.expire(key, 120);
                RedisUtil.zadd("redis:stream", key, System.currentTimeMillis() / 1000);
                RedisUtil.publish("flow", key);
                count++;
                TimeUnit.MILLISECONDS.sleep(800);
            }

        } catch (Exception e) {
            LOGGER.error("error", e);
        }
        return MsgUtils.successMsg();
    }

    @GetMapping("/insertStringZset")
    public String insertStringZset() {
        try {
            //测试
            for (int i = 1; i < 4; i++) {
                String streamPrefix = "stream";
                if (i == 1)
                    RedisUtil.set(streamPrefix + i, i * 10);
                else if (i == 2)
                    RedisUtil.set(streamPrefix + i, 2592000);
                else
                    RedisUtil.set(streamPrefix + i, "");

                //开始制造数据
                for (int j = 0; j < 30; j++) {
                    String zsetKye = "flow:" + streamPrefix + i;
                    String hsetKey = streamPrefix + i + ":yyyyddMM:" + j;
                    for (int k = 0; k < 14; k++) {
                        RedisUtil.hSet(hsetKey, hsetKey +"-"+j+"-"+ k, hsetKey +"-"+j+"-" + k);
                        RedisUtil.expire(hsetKey, ThreadLocalRandom.current().nextInt(30) + 10);
                    }
                    RedisUtil.zadd(zsetKye, hsetKey, LocalDateTime.now().toEpochSecond(ZoneOffset.of("+8")) - 10);
                }
            }
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


        ScanOptions build = ScanOptions.scanOptions().match("redis:*").count(1000).build();
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

        ZSetOperations<String, String> stringStringZSetOperations = SpringContextUtil.getBean(StringRedisTemplate.class).opsForZSet();
        Set<ZSetOperations.TypedTuple<String>> typedTuples = stringStringZSetOperations.rangeByScoreWithScores("redis:zscore", 0, Long.MAX_VALUE, 0, 3);

        return MsgUtils.successMsg();
    }


    @GetMapping("/toWebsocket")
    public String toWebsocket() throws Exception {
        WebSocketClient mWs = new WebSocketClient(new URI("ws://zhc.com:9014/subscribe/strea")) {
            @Override
            public void onOpen(ServerHandshake serverHandshake) {
                System.out.println("链接成功");
            }

            @Override
            public void onMessage(String s) {
                System.out.println(s);
            }

            @Override
            public void onClose(int i, String s, boolean b) {
                System.out.println("关闭");
            }

            @Override
            public void onError(Exception e) {
                e.printStackTrace();
            }
        };
        mWs.connect();
        System.out.println("发发额啊飞啊飞发恶法发");

        TimeUnit.HOURS.sleep(3);
        return null;
    }
}
