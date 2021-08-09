package com.zhouhc.ssewesocket.task;

import com.google.common.util.concurrent.FutureCallback;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//默认的处理异常类，防止没有异常输出, 不然不会有日志出现的
public class ErrorCallBack implements FutureCallback {
    private static final Logger LOGGER = LoggerFactory.getLogger(ErrorCallBack.class);

    @Override
    public void onSuccess(@Nullable Object result) {

    }

    @Override
    public void onFailure(Throwable t) {
        LOGGER.error("线程执行异常", t);
    }
}
