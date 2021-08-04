package com.zhouhc.endpointer.analysis;


import com.google.gson.JsonObject;
import com.zhouhc.endpointer.template.AbsHandler;

//抽象接口的解析类
public interface DataAnalysis {

    //具体的处理方法
    public JsonObject analysisData(AbsHandler absHandler,String toFormatData) throws Exception;
}
