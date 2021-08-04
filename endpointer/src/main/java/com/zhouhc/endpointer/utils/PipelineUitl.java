package com.zhouhc.endpointer.utils;

import com.google.gson.JsonObject;
import com.zhouhc.endpointer.error.CustomException;
import com.zhouhc.endpointer.template.AbsHandler;
import com.zhouhc.endpointer.template.sub.CreateHandler;
import com.zhouhc.endpointer.template.sub.DefaultHandler;
import org.apache.commons.lang3.StringUtils;

import java.util.Collection;
import java.util.Deque;
import java.util.LinkedList;

import static com.zhouhc.endpointer.renum.ErrorEnum.*;

/**
 * 责任链模式的管理类，不对外暴露
 */
public class PipelineUitl {
    //类的静态变量, 也是就是责任链的链表
    private static final Deque<AbsHandler> handlerChain = new LinkedList<AbsHandler>();
    //锁对象
    private static final Object LOCKOBJ = new Object();
    //默认处理的Handler
    private static final AbsHandler defaultHandler = new DefaultHandler();

    static {
        //添加默认的处理器
        handlerChain.addLast(defaultHandler);
    }


    //添加某个stream的处理类
    public static AbsHandler addHandler(JsonObject resquestObj) {
        AbsHandler detailHandler = getNeedJsonObject(resquestObj);
        AbsHandler searchHandler = null;
        synchronized (LOCKOBJ) {
            searchHandler = getHandlerByStreamName(detailHandler.getStreamName());
            if (searchHandler == defaultHandler)
                handlerChain.addFirst(detailHandler);
            else
                detailHandler = searchHandler;
        }
        //只是保存第一次的配置信息
        if (searchHandler == defaultHandler)
            RedisUtil.hSet(ConstantUtil.PPSTATUS, detailHandler.getStreamName(), detailHandler);
        return detailHandler;
    }

    //生成添加用的指定jsonObject
    private static AbsHandler getNeedJsonObject(JsonObject resquestObj) {
        //校验
        if (resquestObj == null)
            throw new CustomException(NOVALIDREGISTRATIONINFORMATION, null);
        String streamName = JSONUtil.toString(resquestObj, "streamName");
        if (StringUtils.isBlank(streamName))
            throw new CustomException(NOSTREAMNAME, null);
        String sourceDataType = JSONUtil.toString(resquestObj, "sourceDataType");
        if (StringUtils.isBlank(sourceDataType))
            throw new CustomException(NOSOURCEDATATYPE, null);
        String sourceDataSchema = JSONUtil.toString(resquestObj, "sourceDataSchema");
        if (StringUtils.isBlank(sourceDataSchema))
            throw new CustomException(NOSOURCEDATASCHEMA, null);
        String storeType = JSONUtil.toString(resquestObj, "storeType");
        if (StringUtils.isBlank(storeType))
            storeType = "redis";
        String pushIP = JSONUtil.toString(resquestObj, "pushIP");
        if (StringUtils.isBlank(pushIP))
            pushIP = "127.0.0.1";
        String pushDataType = JSONUtil.toString(resquestObj, "pushDataType");
        if (StringUtils.isBlank(pushDataType))
            pushDataType = "json";
        String pushDataSchema = JSONUtil.toString(resquestObj, "pushDataSchema");
        if (StringUtils.isBlank(pushDataSchema))
            pushDataSchema = "all";
        return new CreateHandler(streamName, sourceDataType, sourceDataSchema, storeType, pushIP, pushDataType, pushDataSchema);
    }

    //为web启动时，责任链状态恢复单独准备的方法
    public static void initHanderForWebStart(Collection<CreateHandler> createHandlers) {
        if (createHandlers == null || createHandlers.size() == 0)
            return;
        synchronized (LOCKOBJ) {
            createHandlers.stream().forEach(hander -> handlerChain.addFirst(hander));
        }
    }

    //获取某个特定的handler,通过streanName获取名称
    public static AbsHandler getHandlerByStreamName(final String streamName) {
        AbsHandler result = null;
        synchronized (LOCKOBJ) {
            result = handlerChain.stream().filter(handler -> StringUtils.equals(handler.getStreamName(), streamName)).findAny().orElse(defaultHandler);
        }
        return result;
    }

    //删除某个stream
    public static AbsHandler deleteByStreanName(final String streamName) {
        AbsHandler searchHandler = null;
        synchronized (LOCKOBJ) {
            searchHandler = getHandlerByStreamName(streamName);
            //不为null和默认处理handler 的情况
            if (searchHandler != defaultHandler)
                handlerChain.remove(searchHandler);
            else
                searchHandler = null;
        }
        if (searchHandler != null)
            RedisUtil.hDel(ConstantUtil.PPSTATUS, searchHandler.getStreamName());

        return searchHandler;
    }
}
