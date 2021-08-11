package com.zhouhc.ssewesocket.compent;

//id生成器
public class IdGenerateHandler {
    /**
     * 单例
     */
    public volatile static IdGenerateHandler instance;
    /**
     * 开始使用该算法的时间为: 2021-08-10 17:20:43
     */
    private static final long START_TIME = 1628587243000L;
    /**
     * worker id 的bit数，最多支持1024个节点
     */
    private static final int WORKER_ID_BITS = 10;
    /**
     * 序列号，支持单节点最高每毫秒的最大ID数4096
     */
    private final static int SEQUENCE_BITS = 12;
    /**
     * 最大的 worker id ，1023
     * -1 的补码（二进制全1）右移10位, 然后取反
     */
    private final static long MAX_WORKER_ID = ~(-1L << WORKER_ID_BITS);
    /**
     * 最大的序列号，4095
     * -1 的补码（二进制全1）右移12位, 然后取反
     */
    private final static long MAX_SEQUENCE = ~(-1L << SEQUENCE_BITS);
    /**
     * 时间戳的移位
     */
    private final static long TIMESTAMP_LEFT_SHIFT = WORKER_ID_BITS + SEQUENCE_BITS;
    /**
     * 该项目的worker 节点 id
     */
    private final long workerId;
    /**
     * 上次生成ID的时间戳
     */
    private long lastTimestamp = -1L;
    /**
     * 当前毫秒生成的序列
     */
    private long sequence = 0L;

    /**
     * 初始化单例,为每台机器配置id
     * @param workerId 节点Id,最大1024
     * @return the 单例
     */
    public static void init(long workerId) {
        if (instance == null) {
            synchronized (IdGenerateHandler.class) {
                if (instance == null) {
                    // zk分配的workerId过大
                    if (workerId > MAX_WORKER_ID)
                        throw new RuntimeException("工作机器配置错误，机器最大Id不能超过" + MAX_WORKER_ID);
                    //初始化
                    instance = new IdGenerateHandler(workerId);
                }
            }
        }
    }

    //生成id
    public static long nextId() {
        return instance.generateId();
    }

    //构造函数
    private IdGenerateHandler(long workerId) {
        this.workerId = workerId;
    }

    /**
     * 生成唯一id的具体实现
     */
    private synchronized long generateId() {
        long current = System.currentTimeMillis();
        // 如果当前时间小于上一次ID生成的时间戳，说明系统时钟回退过，抛出异常
        if (current < lastTimestamp)
            throw new RuntimeException("系统时间有问题,请校准");

        if (current == lastTimestamp) {
            // 如果当前生成id的时间还是上次的时间，那么对sequence序列号进行+1
            sequence = (sequence + 1) & MAX_SEQUENCE;
            // 当前毫秒生成的序列数已经大于最大值，那么阻塞到下一个毫秒再获取新的时间戳
            if (sequence == MAX_SEQUENCE)
                current = this.nextMs(lastTimestamp);
        } else {
            // 当前的时间戳已经是下一个毫秒
            sequence = 0L;
        }

        // 更新上次生成id的时间戳
        lastTimestamp = current;

        // 进行移位操作生成int64的唯一ID
        //时间戳右移动22位
        long time = (current - START_TIME) << TIMESTAMP_LEFT_SHIFT;
        //workerId 右移动12位
        long tempWordId = this.workerId << SEQUENCE_BITS;
        //唯一的id
        return time | tempWordId | sequence;
    }

    /**
     * 阻塞到下一个毫秒
     */
    private synchronized long nextMs(long timeStamp) {
        long current = System.currentTimeMillis();
        while (current <= timeStamp) {
            current = System.currentTimeMillis();
        }
        return current;
    }

}
