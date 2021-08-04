package com.zhouhc.endpointer.storage.sub;

import com.zhouhc.endpointer.storage.DataStorage;
import com.zhouhc.endpointer.utils.ConstantUtil;
import com.zhouhc.endpointer.utils.GetCofUtil;
import com.zhouhc.endpointer.utils.RedisUtil;
import org.apache.commons.lang3.StringUtils;

//将数据存到redis中
public class RedisDataStorage implements DataStorage {

    private static volatile RedisDataStorage RDS;

    //构造函数私有化
    private RedisDataStorage() {
    }

    //获取实例的方式
    public static RedisDataStorage getInstance() {
        if (RDS == null) {
            synchronized (RedisDataStorage.class) {
                if (RDS == null)
                    RDS = new RedisDataStorage();
            }
        }
        return RDS;
    }

    @Override
    public String storeData(String streamName, String Jsonstr, String originStr) throws Exception {
        //数据保存，如果存在json数据的话，首先保存的
        String result = StringUtils.isNotBlank(Jsonstr) ? Jsonstr : originStr;
        int ttl = GetCofUtil.getValue("custom.redis.ttl", Integer.class, 600);
        RedisUtil.setEx(ConstantUtil.DATAPREFIX + streamName, result, ttl * 1000L);
        return result;
    }
}
