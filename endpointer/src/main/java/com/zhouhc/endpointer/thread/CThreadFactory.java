package com.zhouhc.endpointer.thread;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

//自定义的线程工厂
public class CThreadFactory implements ThreadFactory {
    private static final Logger LOGGER = LoggerFactory.getLogger(CThreadFactory.class);

    //线程名字和计数器
    private final String threadName;
    private final AtomicInteger count;

    public CThreadFactory(String threadName) {
        this.threadName = threadName;
        count = new AtomicInteger(0);
    }

    @Override
    public Thread newThread(Runnable r) {
        Thread thread = new Thread(r);
        thread.setName(String.format("%s-%s", threadName, count.incrementAndGet()));
        thread.setUncaughtExceptionHandler((Thread t, Throwable throwable) -> {
            LOGGER.error(String.format("线程池中的 %s 线程出现异常", t.getName()), throwable);
        });
        return thread;
    }
}
