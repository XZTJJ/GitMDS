package com.zhouhc.ssewesocket.pushway;

import com.zhouhc.ssewesocket.cInt.SendMSInt;
import com.zhouhc.ssewesocket.compent.ClientPipeline;
import com.zhouhc.ssewesocket.sse.ReSseEmitter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.function.Consumer;


//sse的实现类，spring会自己处理
@RestController
@RequestMapping("/subscribe")
public class SseServiceImpl {
    private static final Logger LOGGER = LoggerFactory.getLogger(SseServiceImpl.class);

    @GetMapping(path = "/{channlName}", produces = {MediaType.TEXT_EVENT_STREAM_VALUE})
    public SseEmitter subscribe(@PathVariable("channlName") String channlName) throws Exception {
        // 超时时间设置为1天
        ReSseEmitter sseEmitter = new ReSseEmitter(0L, channlName);
        //一些回调设置, timeout 和 error 方法其实也是调用的completion的方法，SseEmitter内部也会维护一个类似sessionId的东西
        sseEmitter.onTimeout(timeOutCallBack(sseEmitter));
        sseEmitter.onError(errorCallBack(sseEmitter));
        sseEmitter.onCompletion(completionCallBack(sseEmitter));
        //添加倒订阅客户端中
        add(sseEmitter);
        return sseEmitter;
    }

    //添加客户端的方法
    private static void add(final ReSseEmitter sseEmitter) {
        int count = ClientPipeline.getInstance().addClient(sseEmitter.getStreamName(), (SendMSInt) sseEmitter);
        LOGGER.info(String.format("订阅成功，订阅的客户端数量为: %s", count));
    }

    private static void remove(final ReSseEmitter sseEmitter) {
        int count = ClientPipeline.getInstance().removeClient(sseEmitter.getStreamName(), (SendMSInt) sseEmitter);
        LOGGER.info(String.format("取消成功，订阅的客户端数量为: %s", count));
    }

    //错误时调用的方法
    private static Consumer<Throwable> errorCallBack(final ReSseEmitter sseEmitter) {
        return throwable -> {
            remove(sseEmitter);
        };
    }

    //移出客户端的方法
    private static Runnable timeOutCallBack(final ReSseEmitter sseEmitter) {
        return () -> {
            remove(sseEmitter);
        };
    }

    //移出客户端的方法
    private static Runnable completionCallBack(final ReSseEmitter sseEmitter) {
        return () -> {
            remove(sseEmitter);
        };
    }
}
