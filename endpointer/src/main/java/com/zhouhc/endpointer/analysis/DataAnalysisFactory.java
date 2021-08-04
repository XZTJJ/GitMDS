package com.zhouhc.endpointer.analysis;

import com.google.gson.JsonObject;
import com.zhouhc.endpointer.renum.SourceDataType;
import com.zhouhc.endpointer.template.AbsHandler;
import com.zhouhc.endpointer.utils.EnumUtil;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Method;

/**
 * 解析工厂类,负责解析数据
 */
public class DataAnalysisFactory {

    //解析数据
    public static JsonObject analysisToJson(AbsHandler handler, String toForrmatData) throws Exception {
        if (handler == null || StringUtils.isBlank(toForrmatData))
            return null;
        //获取处理类
        SourceDataType enumName = EnumUtil.getEnumByName(SourceDataType.DEFAUTFORMAT, handler.getSourceDataType());
        Class aClass = Class.forName(enumName.getClassName());
        Method getSingleObjMethod = aClass.getMethod(enumName.getSingleObjMethodName());
        DataAnalysis obj = (DataAnalysis) getSingleObjMethod.invoke(null, null);
        return obj.analysisData(handler, toForrmatData);
    }
}
