package com.zhouhc.error;

//自定义的异常信息, 需要打印完成的信息，所以不能这么写
public class CustomExcetion extends RuntimeException {

    private int code;
    private String message;
    private Throwable throwable;

    public CustomExcetion(int code, String message, Throwable throwable) {
        super(message, throwable);
        this.code = code;
        this.message = message;
        this.throwable = throwable;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }


    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Throwable getThrowable() {
        return throwable;
    }

    public void setThrowable(Throwable throwable) {
        this.throwable = throwable;
    }

}
