package com.zhouhc.renum;


/**
 * CreateType 中需要用到的类型,
 * 名称小写，否者会出现两个选项
 */
public enum FunctionType {

    test1(1,"test1"),
    test2(2,"test2")
    ;
    //值
    private int value;
    private String name;

    FunctionType(int value, String name) {
        this.value = value;
        this.name = name;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return this.name;
    }

}
