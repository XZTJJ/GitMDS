package com.zhouhc.ssewesocket.localcache;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.zhouhc.ssewesocket.thread.CThreadFactory;

import java.sql.Time;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.*;

//数据缓存,使用的是 caffine 缓存
public class DataResultCache {

    //因为caffine 本身就是线程安全的所以，不需要同步机制, build 中可以设置原子计算
    private static final Cache<String, Map<String, Object>> LOADINGCACHE = Caffeine.newBuilder().expireAfterWrite(3L, TimeUnit.SECONDS).build();

    public static void addCache(String id) {
        Map<String, Object> valueMap = new HashMap<>();
        valueMap.put("count", 1);
        LOADINGCACHE.put(id, valueMap);
    }

    public static String getData(String id) throws Exception {
        //表示获取完了
        Map<String, Object> valueMap = LOADINGCACHE.getIfPresent(id);
        if (valueMap == null)
            return null;
        synchronized (valueMap) {
            TimeUnit.SECONDS.sleep(10);
            int count = (int) valueMap.get("count");
            valueMap.put("count", count + 1);
            return String.valueOf(count);
        }
    }

    public static void main(String[] args) throws Exception {
//        multiThreadTest();
        remove();
    }

    //多线程测试
    public static void multiThreadTest() throws Exception {
        int count = 2;
        for (int i = 0; i < count; i++)
            addCache(String.valueOf(i));
        int size = 5;
        ExecutorService executorService = Executors.newFixedThreadPool(size, new CThreadFactory("redis-test"));
        int totalTask = 50;
        CountDownLatch countDownLatch = new CountDownLatch(totalTask);

        for (int i = 0; i < totalTask; i++)
            executorService.execute(new TestCount(i % (count + 1), countDownLatch));

        countDownLatch.await();
        executorService.shutdown();
    }

    //删除测试
    public static void remove() throws Exception {
        addCache("0");
        int cout = 10;
        while (cout > 0) {
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        String data = DataResultCache.getData("0");
                        System.out.println(data);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            thread.start();
            TimeUnit.SECONDS.sleep(2);
            cout -= 1;
        }
        TimeUnit.MINUTES.sleep(2);
        System.out.println("");
    }


    private static class TestCount implements Runnable {
        private int id;
        private CountDownLatch countDownLatch;

        public TestCount(int id, CountDownLatch countDownLatch) {
            this.id = id;
            this.countDownLatch = countDownLatch;
        }

        @Override
        public void run() {
            try {
                String data = DataResultCache.getData(String.valueOf(id));
                countDownLatch.countDown();
                System.out.println(id + "-" + data);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
