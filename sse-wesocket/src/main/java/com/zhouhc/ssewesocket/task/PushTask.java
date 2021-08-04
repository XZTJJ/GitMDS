package com.zhouhc.ssewesocket.task;

import com.zhouhc.ssewesocket.cInt.SendMSInt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;


/*  一个客户端推送实例，每一个订阅的客户端都应该是一个异步任务，
 * 防止某个客户端因为网络问题，而影响其他的客户
 */
public class PushTask implements Callable<Void> {
    private static final Logger LOGGER = LoggerFactory.getLogger(PushTask.class);

    private final SendMSInt sendMSInt;
    private final String content;

    public PushTask(SendMSInt sendMSInt, String content) {
        this.sendMSInt = sendMSInt;
        this.content = content;
    }

    @Override
    public Void call() throws Exception {
        try {
            if (this.sendMSInt == null)
                return null;
            sendMSInt.send(content);
            TimeUnit.MILLISECONDS.sleep(200);
            return null;
        } catch (Exception e) {
            LOGGER.error("消息推送失败", e);
            return null;
        }
    }
}
