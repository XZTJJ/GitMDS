package com.zhouhc.endpointer.compent;

import com.zhouhc.endpointer.utils.RedisUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;

//任务处理器
public class RedisInsertTask implements Callable<Void> {
    private static final Logger LOGGER = LoggerFactory.getLogger(RedisInsertTask.class);

    //月,日，分，秒的缩写, YEARS 是用来确定加减时间的
    private final static ChronoUnit[] timeUnit = new ChronoUnit[]{ChronoUnit.MONTHS, ChronoUnit.DAYS, ChronoUnit.HOURS, ChronoUnit.MINUTES, ChronoUnit.SECONDS, ChronoUnit.YEARS};
    //对应单位的最大值
    private final static int[] maxValue = new int[]{12 + 1, 28 + 1, 24 + 1, 60 + 1, 60 + 1, 1 + 1};
    //线程安全的格式化类
    private final static DateTimeFormatter formate = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
    private final static LocalDateTime localDateTime = LocalDateTime.now().minusYears(2);

    private final String name;
    private final CountDownLatch countDownLatch;
    private final AtomicInteger integer;
    private int count;

    public RedisInsertTask(String name, CountDownLatch countDownLatch, AtomicInteger integer) {
        this.name = name;
        this.countDownLatch = countDownLatch;
        this.integer = integer;
    }

    //处理任务
    @Override
    public Void call() throws Exception {
        try {
            if (integer == null)
                return null;
            count = integer.decrementAndGet();
            if (count < 0)
                return null;
            //循环
            String keyTime = getKeyTime();
            String key = String.format("%s:%s:%s", name, keyTime, count);
            //测试数据
            long threadid = Thread.currentThread().getId();
            for (int i = 0; i < 20; i++) {
                String fileName = String.format("%s-%s(%s)-%s(%s)", name, threadid, i, threadid, i);
                String value = String.format("%s-%s(%s)-%s(%s)", name, threadid, i,threadid, i);
                RedisUtil.hSet(key, fileName, value);
            }
            RedisUtil.expire(key, 3600 * 3);
            long epochSecond = LocalDateTime.parse(keyTime, formate).toEpochSecond(ZoneOffset.of("+8"));
            RedisUtil.zadd("flow:"+name,key,epochSecond);
            long levelCount = countDownLatch.getCount();
            if (levelCount % 500 == 0)
                LOGGER.info(String.format("剩余数量:%s", levelCount));
            return null;
        } finally {
            countDownLatch.countDown();
        }
    }


    private String getKeyTime() {
        //具体的值
        int[] value = new int[timeUnit.length];
        //开始测试
        if (timeUnit.length != maxValue.length)
            throw new RuntimeException("数量不匹配");
        //随机值
        for (int i = 0; i < timeUnit.length; i++)
            value[i] = ThreadLocalRandom.current().nextInt(maxValue[i]);
        //最终的时间
        LocalDateTime result = localDateTime;
        //表示减时间，
        if (value[timeUnit.length - 1] == 0) {
            for (int i = 0; i < timeUnit.length - 1; i++)
                result = result.minus(value[i], timeUnit[i]);
        } else if (value[timeUnit.length - 1] == 1) {
            //表示加时间
            for (int i = 0; i < timeUnit.length - 1; i++)
                result = result.plus(value[i], timeUnit[i]);
        } else {
            throw new RuntimeException("不正确的正负值");
        }
        return result.format(formate);
    }
}
