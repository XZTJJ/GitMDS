package com.zhouhc.ssewesocket.uitls;

import cn.hutool.http.HttpRequest;

import java.util.Map;

/**
 * httpUtil 封装类
 */
public class HttpUtil {

    /**
     * @param url 地址
     * @return
     */
    public static String doGet(String url) {
        return HttpRequest.get(url).execute().body();
    }

    /**
     * @param url     地址
     * @param headers 需要额外添加的请求头
     * @return
     */
    public static String doGet(String url, Map<String, String> headers) {
        return HttpRequest.get(url).addHeaders(headers).execute().body();
    }

    /**
     * @param url     地址
     * @param headers 数组形式的请求头, 形式为  key ,value ,key ,value.... ,key 和 value 必须成对出现
     * @return
     */
    public static String doGet(String url, String... headers) {
        //开始计算
        HttpRequest httpRequest = HttpRequest.get(url);
        if (headers.length != 0 && headers.length % 2 == 0) {
            for (int i = 0; i <= headers.length - 2; i += 2)
                httpRequest.header(headers[i], headers[i + 1], false);
        }
        return httpRequest.execute().body();
    }


    /**
     * @param url 地址信息
     * @return
     */
    public static String doPost(String url) {
        return HttpRequest.post(url).execute().body();
    }

    /**
     * @param url     地址信息
     * @param headers 需要额外添加的请求头
     * @return
     */
    public static String doPost(String url, Map<String, String> headers) {
        return HttpRequest.post(url).addHeaders(headers).execute().body();
    }

    /**
     * @param url     地址信息
     * @param headers 需要额外添加的请求头 形式为  key ,value ,key ,value.... ,key 和 value 必须成对出现
     * @return
     */
    public static String doPost(String url, String... headers) {
        HttpRequest httpRequest = HttpRequest.post(url);
        if (headers.length != 0 && headers.length % 2 == 0) {
            for (int i = 0; i <= headers.length - 2; i += 2)
                httpRequest.header(headers[i], headers[i + 1], false);
        }
        return httpRequest.execute().body();

    }

    /**
     * @param url  地址
     * @param body json请求体
     * @return
     */
    public static String doPostWithJson(String url, String body) {
        return HttpRequest.post(url).body(body, "application/json").execute().body();
    }

    /**
     * @param url     地址
     * @param body    json请求体
     * @param headers 需要额外添加的请求头 形式为  key ,value ,key ,value.... ,key 和 value 必须成对出现
     * @return
     */
    public static String doPostWithJson(String url, String body, String... headers) {
        HttpRequest httpRequest = HttpRequest.post(url).body(body, "application/json");
        if (headers.length != 0 && headers.length % 2 == 0) {
            for (int i = 0; i <= headers.length - 2; i += 2)
                httpRequest.header(headers[i], headers[i + 1], false);
        }
        return httpRequest.execute().body();
    }


    /**
     * post 数据通过表单的形式发送数据，使用body体带有参数的情况
     *
     * @param url     地址
     * @param paramMap   post的携带的请求参数
     * @param headers 需要额外添加的请求头 形式为  key ,value ,key ,value.... ,key 和 value 必须成对出现
     * @return
     */
    public static String doPostByForm(String url, Map<String, Object> paramMap, String... headers) {
        HttpRequest httpRequest = HttpRequest.post(url).form(paramMap);
        if (headers.length != 0 && headers.length % 2 == 0) {
            for (int i = 0; i <= headers.length - 2; i += 2)
                httpRequest.header(headers[i], headers[i + 1], false);
        }
        return httpRequest.execute().body();
    }


    /**
     * @param url 删除地址
     * @return
     */
    public static String doDelete(String url) {
        return HttpRequest.delete(url).execute().body();
    }

    /**
     * @param url     地址信息
     * @param headers 需要额外添加的请求头
     * @return
     */
    public static String doDelete(String url, Map<String, String> headers) {
        return HttpRequest.delete(url).addHeaders(headers).execute().body();
    }

    /**
     * @param url     地址信息
     * @param headers 需要额外添加的请求头 形式为  key ,value ,key ,value.... ,key 和 value 必须成对出现
     * @return
     */
    public static String doDelete(String url, String... headers) {
        HttpRequest httpRequest = HttpRequest.delete(url);
        if (headers.length != 0 && headers.length % 2 == 0) {
            for (int i = 0; i <= headers.length - 2; i += 2)
                httpRequest.header(headers[i], headers[i + 1], false);
        }
        return httpRequest.execute().body();
    }
}
