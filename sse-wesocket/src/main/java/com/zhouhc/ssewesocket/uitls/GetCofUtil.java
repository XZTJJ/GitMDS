package com.zhouhc.ssewesocket.uitls;

import org.springframework.core.env.Environment;

//获取spring的配置信息
public class GetCofUtil {

    //获取配置
    public static String getValue(String key) {
        return getEnvironment().getProperty(key);
    }

    public static String getValue(String key,String defalut) {
        return getEnvironment().getProperty(key,defalut);
    }

    //获取配置
    public static <T> T getValue(String key, Class<T> tClass) {
        return getEnvironment().getProperty(key, tClass);
    }

    //带有默认值的获取方式
    public static <T> T getValue(String key, Class<T> tClass, T defaultValue) {
        return getEnvironment().getProperty(key, tClass, defaultValue);
    }

    //获取spring的环境变量
    private static Environment getEnvironment() {
        return SpringContextUtil.getBean(Environment.class);
    }
}
