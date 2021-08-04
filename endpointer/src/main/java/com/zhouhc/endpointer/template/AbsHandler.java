package com.zhouhc.endpointer.template;

import com.google.gson.JsonObject;
import com.zhouhc.endpointer.analysis.DataAnalysisFactory;
import com.zhouhc.endpointer.push.DataPushFactory;
import com.zhouhc.endpointer.storage.DataStorageFactory;

/**
 * 抽象的handler的种类，是所有handler的基础类
 */
public abstract class AbsHandler {
    //stream名称，唯一标识
    private final String streamName;
    //原始数据格式
    private final String sourceDataType;
    //数据的schema
    private final String sourceDataSchema;
    //默认的存储方式,默认是redis
    private final String storeType;
    //数据推送地址,默认为127.0.0.1
    private final String pushIP;
    //数据推送格式，默认为JSON
    private final String pushDataType;
    //推送数据的格式，比如指定显示的数据, 默认显示全部
    private final String pushDataSchema;

    public AbsHandler(String streamName, String sourceDataType, String sourceDataSchema, String pushIP) {
        this.streamName = streamName;
        this.sourceDataType = sourceDataType;
        this.sourceDataSchema = sourceDataSchema;
        this.pushIP = pushIP;
        this.pushDataType = "json";
        this.pushDataSchema = sourceDataSchema;
        this.storeType = "redis";
    }

    public AbsHandler(String streamName, String sourceDataType, String sourceDataSchema, String storeType, String pushIP, String pushDataType, String pushDataSchema) {
        this.streamName = streamName;
        this.sourceDataType = sourceDataType;
        this.sourceDataSchema = sourceDataSchema;
        this.storeType = storeType;
        this.pushIP = pushIP;
        this.pushDataType = pushDataType;
        this.pushDataSchema = pushDataSchema;
    }

    //是否解析钩子函数
    public boolean isNeedAnalysis() {
        return true;
    }

    //是否需要保存
    public boolean isNeedStorage() {
        return true;
    }

    //是否需要推送
    public boolean isNeedPush() {
        return true;
    }

    //具体的处理过程
    public String doHandler(String toFormatData) throws Exception {
        JsonObject jsonObj = null;
        String storeStr = "";
        if (this.isNeedAnalysis())
            jsonObj = DataAnalysisFactory.analysisToJson(this, toFormatData);
        if (this.isNeedStorage())
            storeStr = DataStorageFactory.storeData(this, jsonObj.toString(), toFormatData);
        if (this.isNeedPush())
            DataPushFactory.pushData(this, storeStr);
        return returnMsg();
    }

    public String returnMsg() {
        return "处理成功";
    }

    public String getStreamName() {
        return streamName;
    }

    public String getSourceDataType() {
        return sourceDataType;
    }

    public String getSourceDataSchema() {
        return sourceDataSchema;
    }

    public String getStoreType() {
        return storeType;
    }

    public String getPushIP() {
        return pushIP;
    }

    public String getPushDataType() {
        return pushDataType;
    }

    public String getPushDataSchema() {
        return pushDataSchema;
    }
}
