package com.zhouhc.ssewesocket.thread;

import com.google.common.util.concurrent.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.function.Function;

//全局线程池,为了可以配置线程池大小，做成单例模式
public class GlobalThreadPool {
    private final static Logger LOGGER = LoggerFactory.getLogger(GlobalThreadPool.class);
    //线程数
    private static volatile GlobalThreadPool globalThreadPool;
    //线程数
    private final ListeningExecutorService listeningExecutorService;

    //私有的构造函数
    private GlobalThreadPool(int count) {
        listeningExecutorService = MoreExecutors.listeningDecorator(Executors.newFixedThreadPool(count, new CThreadFactory("flowsnap-thread")));
    }

    //创建实例
    public static void initThreadPool(int count) {
        if (globalThreadPool == null) {
            synchronized (GlobalThreadPool.class) {
                if (globalThreadPool == null) {
                    globalThreadPool = new GlobalThreadPool(count);
                    LOGGER.info(String.format("后台线程数为:%s", count));
                }
            }
        }
    }

    //不需要回调的执行方法
    public static <T> ListenableFuture<T> exec(Callable<T> task) {
        return globalThreadPool.listeningExecutorService.submit(task);
    }

    //带有回调的方法
    public static <T> void exec(Callable<T> task, FutureCallback<T> callback) {
        ListenableFuture<T> submit = globalThreadPool.listeningExecutorService.submit(task);
        Futures.addCallback(submit, callback, globalThreadPool.listeningExecutorService);
    }

    //销毁线程池
    public static void destoryThreadPool() {
        globalThreadPool.listeningExecutorService.shutdown();
        LOGGER.info(String.format("关闭线程池"));
    }
}
