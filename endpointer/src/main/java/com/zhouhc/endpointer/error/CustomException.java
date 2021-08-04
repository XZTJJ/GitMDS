package com.zhouhc.endpointer.error;

import com.zhouhc.endpointer.renum.ErrorEnum;

//自定义错误处理类
public class CustomException extends RuntimeException {
    //错误类型
    private ErrorEnum errorEnum;

    public CustomException(ErrorEnum errorEnum, Throwable cause) {
        super(errorEnum.getMsg(), cause);
        this.errorEnum = errorEnum;
    }

    public ErrorEnum getErrorEnum() {
        return errorEnum;
    }

    public void setErrorEnum(ErrorEnum errorEnum) {
        this.errorEnum = errorEnum;
    }
}
