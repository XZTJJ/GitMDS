package com.zhouhc.endpointer.utils;

import com.zhouhc.endpointer.error.CustomException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.*;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.zhouhc.endpointer.renum.ErrorEnum.redisCursorError;


//Redis的管理类, 存储的都是String类型，如果是非String类,会用gson转成 json字符串的形式
public class RedisUtil {
    private static final Logger LOGGER = LoggerFactory.getLogger(RedisUtil.class);

    /****************************************************  key的操作开始  ******************************************************************/
    //删除某个Key
    public static Boolean delKey(String key) {
        return delKeyBatch(Arrays.asList(key)) == 1;
    }

    //批量删除Key
    public static Long delKeyBatch(Collection<String> keys) {
        if (keys == null)
            return 0L;
        StringRedisTemplate stringRedisTemplate = getStringRedisTemplate();
        return stringRedisTemplate.unlink(keys);
    }

    //获取某个Key的ttl
    public static Long getExpire(String key) {
        if (key == null)
            return 0L;
        StringRedisTemplate stringRedisTemplate = getStringRedisTemplate();
        return stringRedisTemplate.getExpire(key);
    }

    //设置某个Key的ttl
    public static Boolean expire(String key, long timeout, TimeUnit unit) {
        if (key == null)
            return false;
        StringRedisTemplate stringRedisTemplate = getStringRedisTemplate();
        return stringRedisTemplate.expire(key, timeout, unit);
    }

    //设置某个key的ttl，单位毫秒
    public static Boolean expireByMillisecond(String key, long timeout) {
        return expire(key, timeout, TimeUnit.MILLISECONDS);
    }

    //判断是否有某个key
    public static Boolean hasKey(String key) {
        if (key == null)
            return false;
        StringRedisTemplate stringRedisTemplate = getStringRedisTemplate();
        return stringRedisTemplate.hasKey(key);
    }

    //判断给定的Key的存在的数量
    public static Long countExistingKeys(Collection<String> keys) {
        if (keys == null)
            return 0L;
        StringRedisTemplate stringRedisTemplate = getStringRedisTemplate();
        return stringRedisTemplate.countExistingKeys(keys);
    }

    /****************************************************  key的操作结束  ******************************************************************/

    /****************************************************  string类型的操作开始  ******************************************************************/
    //简单的设置某个key
    public static void set(String key, Object value) {
        if (key == null || value == null)
            return;
        ValueOperations<String, String> valueOperations = getStringRedisTemplate().opsForValue();
        String valueStr = JSONUtil.toString(value);
        valueOperations.set(key, valueStr);
    }

    //设置某个Key的过期时间
    public static void setEx(String key, Object value, Long millisecond) {
        setEx(key, value, millisecond, TimeUnit.MILLISECONDS);
    }

    //过期时间的重载版本
    public static void setEx(String key, Object value, Long timeout, TimeUnit timeUnit) {
        if (key == null || value == null || timeUnit == null)
            return;
        ValueOperations<String, String> valueOperations = getStringRedisTemplate().opsForValue();
        String valueStr = JSONUtil.toString(value);
        valueOperations.set(key, valueStr, timeout, timeUnit);
    }

    //设置某个时间过期时间，只有当某个key不存在的时候
    public static void setExNx(String key, Object value, Long millisecond) {
        setExNx(key, value, millisecond, TimeUnit.MILLISECONDS);
    }

    //设置某个时间过期时间，只有当某个key不存在的时候
    public static void setExNx(String key, Object value, Long timeout, TimeUnit timeUnit) {
        if (key == null || value == null || timeUnit == null)
            return;
        ValueOperations<String, String> valueOperations = getStringRedisTemplate().opsForValue();
        String valueStr = JSONUtil.toString(value);
        valueOperations.setIfAbsent(key, valueStr, timeout, timeUnit);
    }

    //当某个Key不存在时设置
    public static void setNx(String key, Object value) {
        if (key == null || value == null)
            return;
        ValueOperations<String, String> valueOperations = getStringRedisTemplate().opsForValue();
        String valueStr = JSONUtil.toString(value);
        valueOperations.setIfAbsent(key, valueStr);
    }

    //获取某个key
    public static String get(String key) {
        if (key == null)
            return "";
        ValueOperations<String, String> valueOperations = getStringRedisTemplate().opsForValue();
        return valueOperations.get(key);
    }

    /****************************************************  string类型的操作结束  ******************************************************************/


    /****************************************************  hash类型的操作开始  ******************************************************************/
    //判断是否存在 hashKey
    public static Boolean hExists(String key, Object hashKey) {
        if (key == null || hashKey == null)
            return false;
        HashOperations<String, Object, Object> hashOperations = getStringRedisTemplate().opsForHash();
        String hashKeystr = "";
        hashKeystr = JSONUtil.toString(hashKey);
        return hashOperations.hasKey(key, hashKeystr);
    }

    //删除某些hashKeys
    public static Long hDel(String key, Object... hashKeys) {
        if (key == null || hashKeys == null)
            return 0L;
        HashOperations<String, Object, Object> hashOperations = getStringRedisTemplate().opsForHash();
        String[] args = Arrays.stream(hashKeys).map(hashKey -> key instanceof String ? (String) hashKey : JSONUtil.toString(hashKey)).toArray(String[]::new);
        return hashOperations.delete(key, args);
    }

    //获取key的长度
    public static Long hLen(String key) {
        if (key == null)
            return 0L;
        HashOperations<String, Object, Object> hashOperations = getStringRedisTemplate().opsForHash();
        return hashOperations.size(key);
    }

    //设置某个Key
    public static void hSet(String key, Object hashKey, Object value) {
        if (key == null || hashKey == null || value == null)
            return;
        HashOperations<String, Object, Object> hashOperations = getStringRedisTemplate().opsForHash();
        String hashKeyStr = JSONUtil.toString(hashKey);
        String valueStr = JSONUtil.toString(value);
        hashOperations.put(key, hashKeyStr, valueStr);
    }

    //获取某个Key
    public static String hGet(String key, Object hashKey) {
        if (key == null || hashKey == null)
            return "";
        HashOperations<String, Object, Object> hashOperations = getStringRedisTemplate().opsForHash();
        String hashKeyStr = JSONUtil.toString(hashKey);
        return hashOperations.get(key, hashKeyStr) + "";
    }

    //获取某个Key,并转换成特定的值
    public static <T> T hget(String key, Object hashKey, Class<T> targetClass) {
        return JSONUtil.toT(hGet(key, hashKey), targetClass);
    }

    //获取获取所有值
    public static Map<String, String> hGetAll(String key) {
        return hGetAll(key,String.class,String.class);
    }

    //获取获取所有值
    public static <T, V> Map<T, V> hGetAll(String key, Class<T> hashKeyClassType, Class<V> valueClassType) {
        if (key == null)
            return new HashMap<T, V>();
        //获取数据
        HashOperations<String, Object, Object> hashOperations = getStringRedisTemplate().opsForHash();
        ScanOptions scanOptions = ScanOptions.scanOptions().match("*").count(1000).build();
        //游标遍历
        try (Cursor<Map.Entry<Object, Object>> cursor = hashOperations.scan(key, scanOptions)) {
            Map<T, V> resultMap = cursor.stream().collect(Collectors.toMap(tempKey -> JSONUtil.toT(tempKey.getKey() + "", hashKeyClassType),
                    tempKey -> JSONUtil.toT(tempKey.getValue() + "", valueClassType)));
            return resultMap;
        } catch (Exception e) {
            throw new CustomException(redisCursorError, e);
        }
    }

    //获取获取keys
    public static Set<String> hKeys(String key) {
        return hGetAll(key).keySet();
    }

    //获取所有的keys带有转换类型的
    public static <T> Set<T> hKeys(String key, Class<T> hashKeyClassType) {
        return hGetAll(key, hashKeyClassType, String.class).keySet();
    }

    //获取获取values
    public static Collection<String> hVals(String key) {
        return hGetAll(key).values();
    }

    //获取所有的keys带有转换类型的
    public static <T> Collection<T> hVals(String key, Class<T> valueTypeClass) {
        return hGetAll(key, String.class, valueTypeClass).values();
    }

    /****************************************************  hash类型的操作结束  ******************************************************************/

    //获取Spring自带的管理类
    private static StringRedisTemplate getStringRedisTemplate() {
        return SpringContextUtil.getBean(StringRedisTemplate.class);
    }
}
