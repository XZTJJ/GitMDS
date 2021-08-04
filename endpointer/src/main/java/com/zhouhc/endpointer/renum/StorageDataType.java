package com.zhouhc.endpointer.renum;

//数据存储枚举类，定义定义存储该存储方式 的类名和获取 实例的 方法名(一般实例采用单例模式)
public enum StorageDataType {

    REDIS("com.zhouhc.endpointer.storage.sub.RedisDataStorage", "getInstance"),
    DEFAULTSTORAGE("com.zhouhc.endpointer.storage.sub.RedisDataStorage", "getInstance");

    //类名
    private String className;
    //获取单实例的方法名
    private String singleObjMethodName;

    StorageDataType(String className, String singleObjMethodName) {
        this.className = className;
        this.singleObjMethodName = singleObjMethodName;
    }

    public String getClassName() {
        return className;
    }

    public String getSingleObjMethodName() {
        return singleObjMethodName;
    }
}
