package com.zhouhc.endpointer.renum;

//统一定义的一些异常
public enum ErrorEnum {
    //一些错误定义
    UNKNOWN(500, "服务器为止错误"),
    NOANALYSISCLASS(500, "没有该解析方法"),
    NOVALIDREGISTRATIONINFORMATION(500, "没有提供有效的注册信息"),
    NOSTREAMNAME(500, "没有stream Name"),
    NOSOURCEDATATYPE(500, "没有指定源数据的格式"),
    NOSOURCEDATASCHEMA(500, "没有指定源数据的Schema"),
    redisCursorError(500, "redis游标遍历错误"),
    JSONADDERROR(500, "json原始添加错误"),
    WEBSTARTERROR(500, "web服务器启动错误"),
    ;


    private int code;
    private String msg;

    ErrorEnum(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }
}
