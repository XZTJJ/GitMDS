package com.zhouhc.endpointer.utils;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import com.zhouhc.endpointer.error.CustomException;

import java.io.*;

import static com.zhouhc.endpointer.renum.ErrorEnum.JSONADDERROR;

/**
 * JSON 序列化 和 反序列化方式
 */
public class JSONUtil {
    private static final GsonBuilder builder = new GsonBuilder();

    //是否为一个json格式
    public static boolean isJson(String result) {
        return isJson(new StringReader(result));
    }

    public static boolean isJson(File file) {
        try {
            return isJson(new FileReader(file));
        } catch (FileNotFoundException e) {
            return false;
        }
    }

    private static boolean isJson(Reader reader) {
        JsonElement readJson;
        try {
            readJson = JsonParser.parseReader(reader);
        } catch (Exception e) {
            return false;
        }
        if (readJson == null) {
            return false;
        }
        if (!readJson.isJsonObject()) {
            return false;
        }
        return true;
    }


    //各种类型的获取
    public static String toString(JsonObject resultJson, String key) {
        if (!resultJson.has(key))
            return "";
        JsonElement element = resultJson.get(key);
        return element.isJsonNull() ? "" : resultJson.get(key).getAsString();
    }

    //pojo to String
    public static String toString(Object obj) {
        if (obj instanceof String)
            return (String) obj;
        return builder.create().toJson(obj);
    }

    //string to pojo
    public static JsonObject toJson(String str) {
        return builder.create().fromJson(str, JsonObject.class);
    }

    //file to json str
    public static JsonObject toJson(File file) throws FileNotFoundException {
        return builder.create().fromJson(new FileReader(file), JsonObject.class);
    }

    public static JsonObject toJson(JsonObject resultJson, String key) {
        return resultJson.getAsJsonObject(key);
    }

    public static JsonObject toJson(JsonArray resultJson, int index) {
        return resultJson.get(index).getAsJsonObject();
    }

    public static JsonArray toArray(JsonObject resultJson, String key) {
        return resultJson.getAsJsonArray(key);
    }

    public static int toInt(JsonObject resultJson, String key) {
        return resultJson.get(key).getAsInt();
    }

    public static boolean toBoolean(JsonObject resultJson, String key) {
        return resultJson.get(key).getAsBoolean();
    }

    public static double toDouble(JsonObject resultJson, String key) {
        return resultJson.get(key).getAsDouble();
    }

    public static <T> T toT(JsonObject resultJson, String key, Class<T> targetClass) {
        return toT(toString(resultJson, key), targetClass);
    }

    public static <T> T toT(String jsonStr, Class<T> targetClass) {
        return builder.create().fromJson(jsonStr, targetClass);
    }

    public static <T> T toCollection(JsonObject resultJson, String key, TypeToken typeOfT) {
        return toCollection(toString(resultJson, key), typeOfT);
    }

    public static <T> T toCollection(String jsonStr, TypeToken typeOfT) {
        return builder.create().fromJson(jsonStr, typeOfT.getType());
    }

    //将某个对象转成JsonElement
    public static JsonElement toJsonElement(Object object) {
        if(object instanceof  String)
            return builder.create().fromJson((String)object,JsonElement.class);
        return builder.create().toJsonTree(object);
    }

    //将对象的值 作为 JsonElement 获取
    public static JsonElement toJsonElement(JsonObject resultJson, String key) {
        if (resultJson == null || key == null)
            return JsonNull.INSTANCE;
        return toJsonElement(resultJson.get(key));
    }

    //简单的生成Json的方法
    public static JsonObject getJsonFromArrays(Object... args) {
        if (args.length % 2 != 0)
            throw new CustomException(JSONADDERROR, null);
        JsonObject jsonObj = new JsonObject();
        for (int i = 0; i < args.length; i += 2) {
            String key = args[i] + "";
            if (args[i + 1] == null)
                continue;
            else if (args[i + 1] instanceof String)
                jsonObj.addProperty(key, (String) args[i + 1]);
            else if (args[i + 1] instanceof Number)
                jsonObj.addProperty(key, (Number) args[i + 1]);
            else if (args[i + 1] instanceof Boolean)
                jsonObj.addProperty(key, (Boolean) args[i + 1]);
            else if (args[i + 1] instanceof Character)
                jsonObj.addProperty(key, (Character) args[i + 1]);
            else if (args[i + 1] instanceof JsonElement)
                jsonObj.add(key, (JsonElement) args[i + 1]);
            else
                jsonObj.add(key, toJsonElement(args[i + 1]));
        }
        return jsonObj;
    }

}
