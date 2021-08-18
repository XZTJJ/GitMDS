package com.zhouhc.endpointer.utils;

import com.google.gson.JsonObject;
import com.zhouhc.endpointer.error.CustomException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.StringRedisConnection;
import org.springframework.data.redis.core.*;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.Topic;

import java.nio.charset.Charset;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

import static com.zhouhc.endpointer.renum.ErrorEnum.redisCursorError;


//Redis的管理类, 存储的都是String类型，如果是非String类,会用gson转成 json字符串的形式
public class RedisUtil {
    private static final Logger LOGGER = LoggerFactory.getLogger(RedisUtil.class);
    //订阅消息时用到
    private final static Object lockObj = new Object();

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
    public static Boolean expire(String key, long timeout) {
        return expire(key, timeout, TimeUnit.SECONDS);
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

    //redis的Key扫描
    public static <T> Set<T> scan(String pattern, int limit, Class<T> keyType) {
        ScanOptions build = ScanOptions.scanOptions().match(pattern).count(limit).build();
        RedisConnection connection = getStringRedisTemplate().getConnectionFactory().getConnection();
        try (Cursor<byte[]> cursor = connection.scan(build)) {
            return cursor.stream().map(byteArrays -> new String(byteArrays, Charset.forName("utf-8"))).map(str -> JSONUtil.toT(str, keyType)).collect(Collectors.toCollection(LinkedHashSet::new));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
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
        return (String) hashOperations.get(key, hashKeyStr);
    }

    //获取某个Key,并转换成特定的值
    public static <T> T hGet(String key, Object hashKey, Class<T> targetClass) {
        return JSONUtil.toT(hGet(key, hashKey), targetClass);
    }

    //获取获取所有值
    public static Map<String, String> hGetAll(String key) {
        return hGetAll(key, String.class, String.class);
    }

    //获取获取所有值
    public static <T, V> Map<T, V> hGetAll(String key, Class<T> hashKeyClassType, Class<V> valueClassType) {
        if (key == null)
            return Collections.EMPTY_MAP;
        return hScan(key, "*", 2000, hashKeyClassType, valueClassType);
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

    //hash方式的游标遍历,
    public static <T, V> Map<T, V> hScan(String key, String pattern, int limit, Class<T> hashKeyClassType, Class<V> valueClassType) {
        HashOperations<String, Object, Object> hashOperations = getStringRedisTemplate().opsForHash();
        ScanOptions scanOptions = ScanOptions.scanOptions().match(pattern).count(limit).build();
        try (Cursor<Map.Entry<Object, Object>> cursor = hashOperations.scan(key, scanOptions);) {
            Map<T, V> resultMap = cursor.stream().collect(Collectors.toMap(tempKey -> JSONUtil.toT((String) tempKey.getKey(), hashKeyClassType),
                    tempKey -> JSONUtil.toT((String) tempKey.getValue(), valueClassType)));
            return resultMap;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    //一次性获取多个值
    public static List<Object> multiHGet(final Set<String> keys) {
        List<Object> result = getStringRedisTemplate().executePipelined(new RedisCallback<Long>() {
            @Override
            public Long doInRedis(RedisConnection connection) throws DataAccessException {
                StringRedisConnection stringRedisConn = (StringRedisConnection) connection;
                //结果逐一返回
                for (String key : keys)
                    stringRedisConn.append("", "");
                return null;
            }
        });
        return result;
    }

    /****************************************************  hash类型的操作结束  ******************************************************************/


    /****************************************************  zset类型的操作结束  ******************************************************************/
    //zset的增加操作
    public static boolean zadd(String key, Object value, double score) {
        ZSetOperations<String, String> stringStringZSetOperations = getStringRedisTemplate().opsForZSet();
        String valueStr = JSONUtil.toString(value);
        if (key == null || valueStr == null)
            return false;
        return stringStringZSetOperations.add(key, valueStr, score);
    }

    //zset的删除操作
    public static long zrem(String key, Object... value) {
        ZSetOperations<String, String> stringStringZSetOperations = getStringRedisTemplate().opsForZSet();
        if (key == null || value.length == 0)
            return 0;
        AtomicLong atomicLong = new AtomicLong(0);
        Arrays.stream(value).map(val -> JSONUtil.toString(val)).forEach(valstr -> atomicLong.addAndGet(stringStringZSetOperations.remove(key, valstr)));
        return atomicLong.get();
    }

    //zset的按照socre进行游标遍历
    public static <T> Set<T> zRangeByScore(String key, double min, double max, long count, Class<T> valueClassType) {
        if (key == null)
            return Collections.EMPTY_SET;
        //分段获取,
        long zcount = zcount(key, min, max);
        long increamt = 0;
        ZSetOperations<String, String> stringStringZSetOperations = getStringRedisTemplate().opsForZSet();
        Set<T> result = new LinkedHashSet<T>();
        while (increamt < zcount) {
            Set<T> collect = stringStringZSetOperations.rangeByScore(key, min, max, increamt, count).stream().map(valueStr -> JSONUtil.toT(valueStr, valueClassType)).collect(Collectors.toCollection(LinkedHashSet::new));
            result.addAll(collect);
            increamt += count;
        }
        return result;
    }

    //zset的按照socre进行游标遍历，简化版本
    public static Set<String> zRangeByScore(String key, double min, double max) {
        return zRangeByScore(key, min, max, 2000, String.class);
    }

    //zset的按照socre进行游标遍历，简化版本
    public static Set<String> zRangeByScore(String key, double min, double max, long offset, long count) {
        return getStringRedisTemplate().opsForZSet().rangeByScore(key, min, max, offset, count);
    }

    //zset的count操作
    public static long zcount(String key, double min, double max) {
        if (key == null)
            return 0;
        return getStringRedisTemplate().opsForZSet().count(key, min, max);
    }

    //zset的count操作
    public static long zcard(String key) {
        if (key == null)
            return 0;
        return getStringRedisTemplate().opsForZSet().zCard(key);
    }

    /****************************************************  zset类型的操作结束  ******************************************************************/


    /****************************************************  pub/sub形式  ************************************************************************/
    //RedisMessageListenerContainer 提示如果同事增加或者删除listner会出现问题，所以同步一下
    public static void subscribe(MessageListener listener, Topic topic) {
        RedisMessageListenerContainer redisMessageListenerContainer = getRedisMessageListenerContainer();
        synchronized (lockObj) {
            redisMessageListenerContainer.addMessageListener(listener, topic);
        }
    }

    //RedisMessageListenerContainer 提示如果同事增加或者删除listner会出现问题，所以同步一下
    public static void unSubscribe(MessageListener listener, Topic topic) {
        RedisMessageListenerContainer redisMessageListenerContainer = getRedisMessageListenerContainer();
        synchronized (lockObj) {
            redisMessageListenerContainer.removeMessageListener(listener, topic);
        }
    }

    //Redis的push的发送消息
    public static void publish(String channelName, Object message) {
        if (channelName == null || message == null)
            return;
        getStringRedisTemplate().convertAndSend(channelName, JSONUtil.toString(message));
    }

    /****************************************************  pub/sub形式  ************************************************************************/

    //获取Spring自带的管理类
    private static StringRedisTemplate getStringRedisTemplate() {
        return SpringContextUtil.getBean(StringRedisTemplate.class);
    }

    //获取Spring的监听容器
    private static RedisMessageListenerContainer getRedisMessageListenerContainer() {
        return SpringContextUtil.getBean(RedisMessageListenerContainer.class);
    }
}
