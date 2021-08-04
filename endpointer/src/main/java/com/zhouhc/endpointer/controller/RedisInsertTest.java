package com.zhouhc.endpointer.controller;

import com.zhouhc.endpointer.compent.RedisInsertTask;
import com.zhouhc.endpointer.thread.CThreadFactory;
import com.zhouhc.endpointer.utils.GetCofUtil;
import com.zhouhc.endpointer.utils.MsgUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
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
            executors = Executors.newFixedThreadPool(threadSize,new CThreadFactory("redis-insert"));
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


}
