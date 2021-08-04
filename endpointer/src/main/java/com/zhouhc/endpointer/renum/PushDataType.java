package com.zhouhc.endpointer.renum;

public enum PushDataType {
    JSON("com.zhouhc.endpointer.push.sub.RemoteJsonDataPush", "getInstance"),
    DEFAULTPUSH("com.zhouhc.endpointer.push.sub.RemoteJsonDataPush", "getInstance");


    //类名
    private String className;
    //获取单实例的方法名
    private String singleObjMethodName;

    PushDataType(String className, String singleObjMethodName) {
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
