package com.zhouhc.ssewesocket.config;

import com.zhouhc.ssewesocket.thread.GlobalThreadPool;
import com.zhouhc.ssewesocket.uitls.GetCofUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.context.WebServerInitializedEvent;

import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

//主要作用是监听Spring的相关事件，处理一些其他工作
@Component
public class ExtraWorkConfig {
    private static final Logger LOGGER = LoggerFactory.getLogger(ExtraWorkConfig.class);

    //spring的web容器初始化
    @EventListener
    public void springBootWebInit(WebServerInitializedEvent webServerInitializedEvent) {
        //最开始做的 -- 创建线程池
        GlobalThreadPool.initThreadPool(GetCofUtil.getValue("flow-thread-counts", Integer.class, 10));
    }

    //spring context刷新监听
    @EventListener
    public void springBootRefreshed(ContextRefreshedEvent contextRefreshedEvent) {
    }

    //spring关闭后的事件
    @EventListener
    public void springBootClose(ContextClosedEvent contextClosedEvent) {
        //最后处理
        GlobalThreadPool.destoryThreadPool();
    }
}
