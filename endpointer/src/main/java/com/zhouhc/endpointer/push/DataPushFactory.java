package com.zhouhc.endpointer.push;

import com.zhouhc.endpointer.renum.PushDataType;
import com.zhouhc.endpointer.template.AbsHandler;
import com.zhouhc.endpointer.utils.EnumUtil;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Method;

//数据推送工厂，作用 : 组装用户需要的数据，并把这些数据推送给用户
public class DataPushFactory {
    //解析数据
    public static String pushData(AbsHandler handler, String storeStr) throws Exception {
        if (handler == null || StringUtils.isBlank(storeStr))
            return null;
        //获取处理类
        PushDataType enumName = EnumUtil.getEnumByName(PushDataType.DEFAULTPUSH, handler.getPushDataType());
        Class aClass = Class.forName(enumName.getClassName());
        Method getSingleObjMethod = aClass.getMethod(enumName.getSingleObjMethodName());
        DataPush dataStorage = (DataPush) getSingleObjMethod.invoke(null, null);
        String message = dataStorage.getOnlyNeedJsonStr(handler, storeStr);
        dataStorage.pushMessage(handler.getStreamName(), message);
        return message;
    }
}
