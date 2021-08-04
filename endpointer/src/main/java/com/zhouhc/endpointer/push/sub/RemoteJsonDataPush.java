package com.zhouhc.endpointer.push.sub;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.zhouhc.endpointer.push.DataPush;
import com.zhouhc.endpointer.template.AbsHandler;
import com.zhouhc.endpointer.utils.JSONUtil;
import com.zhouhc.endpointer.utils.MessagePushUtil;
import org.apache.commons.lang3.StringUtils;

//解析JSON数据并且负责解析推送
public class RemoteJsonDataPush implements DataPush {

    private static volatile RemoteJsonDataPush RJD;

    private RemoteJsonDataPush() {

    }

    //获取实例对象
    public static RemoteJsonDataPush getInstance() {
        if (RJD == null) {
            synchronized (RemoteJsonDataPush.class) {
                if (RJD == null)
                    RJD = new RemoteJsonDataPush();
            }
        }
        return RJD;
    }

    @Override
    public String getOnlyNeedJsonStr(AbsHandler handler, String storeData) {
        //获取分隔符
        String[] pros = StringUtils.split(handler.getPushDataSchema(), ",");
        //判断是否为数据
        String resultStr = "";
        JsonElement element = JSONUtil.toJsonElement(storeData);
        if (element.isJsonObject())
            resultStr = copyJsonObjectPro(handler.getStreamName(), element.getAsJsonObject(), pros).toString();
        //处理数组的情况
        if (element.isJsonArray()) {
            JsonArray asJsonArray = element.getAsJsonArray();
            JsonArray resultArrays = new JsonArray();
            for (JsonElement obj : asJsonArray)
                resultArrays.add(copyJsonObjectPro(handler.getStreamName(), obj.getAsJsonObject(), pros));
            resultStr = resultArrays.toString();
        }
        return resultStr;
    }

    //获取元素
    private JsonObject copyJsonObjectPro(String streamName, JsonObject sourceJson, String[] pros) {
        JsonObject returnJson = new JsonObject();
        if (sourceJson == null || pros == null)
            return returnJson;
        //准备开始解析
        for (String pro : pros)
            returnJson.add(pro, JSONUtil.toJsonElement(sourceJson, pro));
        //加入streamName的属性
        returnJson.addProperty("streamName", streamName);
        return returnJson;
    }

    //推送数据
    @Override
    public String pushMessage(String streamName, String needMessage) {
        MessagePushUtil.pushDataToRemote(streamName, needMessage);
        return needMessage;
    }
}
