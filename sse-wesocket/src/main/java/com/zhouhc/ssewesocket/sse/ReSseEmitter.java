package com.zhouhc.ssewesocket.sse;

import com.zhouhc.ssewesocket.cInt.SendMSInt;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;

//sse协议组件，重写SseEmitter,统一发送逻辑
public class ReSseEmitter extends SseEmitter implements SendMSInt {
    //调用原来的发送方式
    private String streamName;

    public ReSseEmitter(Long timeout, String streamName) {
        super(timeout);
        this.streamName = streamName;
    }

    @Override
    public void send(String message) throws IOException {
        //多个任务下，防止出现抢夺同一个ReSseEmitter
        synchronized (this) {
            super.send(message);
        }
    }

    public String getStreamName() {
        return this.streamName;
    }
}
