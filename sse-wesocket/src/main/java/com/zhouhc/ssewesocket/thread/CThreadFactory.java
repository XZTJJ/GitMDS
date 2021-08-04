package com.zhouhc.ssewesocket.thread;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

//自定义的线程工厂
public class CThreadFactory implements ThreadFactory {

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
        return thread;
    }
}
