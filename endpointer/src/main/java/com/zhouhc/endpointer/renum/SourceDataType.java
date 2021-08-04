package com.zhouhc.endpointer.renum;

//数据解析枚举类，定义定义解析该格式数据 的类名和获取 实例的 方法名(一般实例采用单例模式)
public enum SourceDataType {

    JSON("com.zhouhc.endpointer.analysis.sub.JsonDataAnalysis", "getInstance"),
    DEFAUTFORMAT("com.zhouhc.endpointer.analysis.sub.JsonDataAnalysis", "getInstance");

    //类名
    private String className;
    //获取单实例的方法名
    private String singleObjMethodName;

    SourceDataType(String className, String singleObjMethodName) {
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
