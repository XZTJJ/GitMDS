package com.zhouhc.endpointer.analysis.sub;

import com.google.gson.JsonObject;
import com.zhouhc.endpointer.analysis.DataAnalysis;
import com.zhouhc.endpointer.template.AbsHandler;
import com.zhouhc.endpointer.utils.JSONUtil;

//具体的Json处理类型,保证只有唯一的一个实例
public class JsonDataAnalysis implements DataAnalysis {

    private static volatile JsonDataAnalysis JDA;

    //构造函数私有化
    private JsonDataAnalysis() {
    }

    //获取实例的方式
    public static JsonDataAnalysis getInstance() {
        if (JDA == null) {
            synchronized (JsonDataAnalysis.class) {
                if (JDA == null)
                    JDA = new JsonDataAnalysis();
            }
        }
        return JDA;
    }

    @Override
    public JsonObject analysisData(AbsHandler absHandler, String toFormatData) throws Exception {
        return JSONUtil.toJson(toFormatData);
    }
}
