package com.zhouhc.endpointer.storage;


import com.zhouhc.endpointer.renum.StorageDataType;
import com.zhouhc.endpointer.template.AbsHandler;
import com.zhouhc.endpointer.utils.EnumUtil;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Method;

/**
 * 保存数据的工厂
 */
public class DataStorageFactory {

    //解析数据
    public static String storeData(AbsHandler handler, String Jsonstr, String originStr) throws Exception {
        if (handler == null || (StringUtils.isBlank(Jsonstr) && StringUtils.isBlank(originStr)))
            return null;
        //获取处理类
        StorageDataType enumName = EnumUtil.getEnumByName(StorageDataType.DEFAULTSTORAGE, handler.getStoreType());
        Class aClass = Class.forName(enumName.getClassName());
        Method getSingleObjMethod = aClass.getMethod(enumName.getSingleObjMethodName());
        DataStorage dataStorage = (DataStorage) getSingleObjMethod.invoke(null, null);
        return dataStorage.storeData(handler.getStreamName(), Jsonstr, originStr);
    }
}
