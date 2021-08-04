package com.zhouhc.ssewesocket.pushway;

import com.zhouhc.ssewesocket.cInt.SendMSInt;
import com.zhouhc.ssewesocket.compent.ClientPipeline;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;

//websocket的实现类，spring会为每个连接进来的客户端创建一个实例
@ServerEndpoint("/subscribe/{channlName}")
@Service
public class WebSocketServiceImpl implements SendMSInt {
    private final static Logger LOGGER = LoggerFactory.getLogger(WebSocketServiceImpl.class);
    //保存stream的名字
    private String channlName;
    //保存会话
    private Session session;

    @OnOpen
    public void onOpen(Session session, @PathParam("channlName") String channlName) {
        this.session = session;
        this.channlName = channlName;
        int count = ClientPipeline.getInstance().addClient(channlName, this);
        LOGGER.info(String.format("订阅成功，订阅的客户端数量为: %s", count));
    }

    /**
     * 连接关闭调用的方法
     */
    @OnClose
    public void onClose() {
        int count = ClientPipeline.getInstance().removeClient(this.getChannlName(), this);
        LOGGER.info(String.format("取消成功，订阅的客户端数量为: %s", count));
    }

    /**
     * 收到客户端消息后调用的方法
     */
    @OnMessage
    public void onMessage(String message, Session session) {
        LOGGER.info(String.format("客户端发送的消息为: ", message));
    }

    /**
     * 发生错误时调用
     */
    @OnError
    public void onError(Session session, Throwable error) {
        int count = ClientPipeline.getInstance().removeClient(this.getChannlName(), this);
        LOGGER.error("某一个客户端发送消息失败", error);
    }

    @Override
    public void send(String message) throws IOException {
        synchronized (this) {
            this.session.getBasicRemote().sendText(message);
        }
    }

    public String getChannlName() {
        return this.channlName;
    }
}
