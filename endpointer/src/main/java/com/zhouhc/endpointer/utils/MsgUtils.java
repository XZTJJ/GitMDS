package com.zhouhc.endpointer.utils;

import com.google.gson.JsonObject;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class MsgUtils {

    public static String successMsg() {
        JsonObject msg = new JsonObject();
        msg.addProperty("msg", "操作成功");
        msg.addProperty("code", 200);
        return msg.toString();
    }

    public static String successMsg(Object data) {
        JsonObject msg = new JsonObject();
        msg.addProperty("msg", "操作成功");
        msg.addProperty("code", 200);
        msg.add("data", JSONUtil.toJsonElement(data));
        return msg.toString();
    }

    public static void successMsg(HttpServletResponse response) throws IOException {
        JsonObject msg = new JsonObject();
        msg.addProperty("msg", "操作成功");
        msg.addProperty("code", 200);
        response.setContentType("application/json;charset=utf-8");
        response.getWriter().write(msg.toString());
    }

    public static String ErrorMsg() {
        JsonObject msg = new JsonObject();
        msg.addProperty("msg", "未知错误，请联系开发人员");
        msg.addProperty("code", 500);
        return msg.toString();
    }

    public static void ErrorMsg(HttpServletResponse response) throws IOException {
        JsonObject msg = new JsonObject();
        msg.addProperty("msg", "未知错误，请联系开发人员");
        msg.addProperty("code", 500);
        response.setContentType("application/json;charset=utf-8");
        response.getWriter().write(msg.toString());
    }

    public static String OtherMsg(int code, String Message) {
        JsonObject msg = new JsonObject();
        msg.addProperty("msg", Message);
        msg.addProperty("code", code);
        return msg.toString();
    }

    public static void OtherMsg(int code, String Message, HttpServletResponse response) throws IOException {
        JsonObject msg = new JsonObject();
        msg.addProperty("msg", Message);
        msg.addProperty("code", code);
        response.setContentType("application/json;charset=utf-8");
        response.getWriter().write(msg.toString());
    }
}
