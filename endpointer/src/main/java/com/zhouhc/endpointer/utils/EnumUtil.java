package com.zhouhc.endpointer.utils;

import com.zhouhc.endpointer.error.CustomException;
import com.zhouhc.endpointer.renum.ErrorEnum;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class EnumUtil {

    //通过名字来获取枚举类对象
    public static <T extends Enum> T getEnumByName(T enumObj, String name) throws NoSuchMethodException,
            IllegalAccessException, InvocationTargetException {
        //类型信息
        Class enumClass = enumObj.getClass();
        //获取所有的常量并且通过名称来比较
        T[] allEnums = (T[]) enumClass.getEnumConstants();
        Method nameMethod = enumClass.getMethod("name");
        T result = null;
        for (T t : allEnums) {
            if (StringUtils.equalsIgnoreCase(name, nameMethod.invoke(t).toString())) {
                result = t;
                break;
            }
        }
        //没有该类直接报错
        if (result == null)
            throw new CustomException(ErrorEnum.NOANALYSISCLASS, null);
        return result;
    }

}
