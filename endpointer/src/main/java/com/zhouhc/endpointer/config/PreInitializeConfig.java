package com.zhouhc.endpointer.config;

import com.zhouhc.endpointer.template.sub.CreateHandler;
import com.zhouhc.endpointer.utils.ConstantUtil;
import com.zhouhc.endpointer.utils.PipelineUitl;
import com.zhouhc.endpointer.utils.RedisUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.context.WebServerInitializedEvent;
import org.springframework.boot.web.server.WebServer;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Collection;

//web启动事件监听，在项目启动前做一些初始化工作
@Component
public class PreInitializeConfig implements ApplicationListener<WebServerInitializedEvent> {
    private static final Logger LOGGER = LoggerFactory.getLogger(PreInitializeConfig.class);

    //启动前监听
    @Override
    public void onApplicationEvent(WebServerInitializedEvent event) {
        try {
            //获取地址和端口
            WebServer webServer = event.getWebServer();
            ConstantUtil.Post = webServer.getPort();
            ConstantUtil.HostAddr = InetAddress.getLocalHost().getHostAddress();
            //状态的恢复
            Collection<CreateHandler> createHandlers = RedisUtil.hVals(ConstantUtil.PPSTATUS, CreateHandler.class);
            PipelineUitl.initHanderForWebStart(createHandlers);
            LOGGER.info("handlers初始化完成,总共加载了{}个handler...",createHandlers.size());
        } catch (UnknownHostException e) {
            LOGGER.error("获取IP,端口，或者redis数据失败", e);
        }

    }
}
