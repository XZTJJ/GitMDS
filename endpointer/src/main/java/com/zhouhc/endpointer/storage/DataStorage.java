package com.zhouhc.endpointer.storage;

//存储数据的统一接口
public interface DataStorage {

    //抽象的存储类的方式
    public String storeData(String streamName, String Jsonstr, String originStr) throws Exception;
}
