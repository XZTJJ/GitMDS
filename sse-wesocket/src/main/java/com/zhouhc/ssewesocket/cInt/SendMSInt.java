package com.zhouhc.ssewesocket.cInt;

import java.io.IOException;

//发送数据接口,只有一个发送方法
public interface SendMSInt {
    //发送方法
    public void send(String message) throws IOException;
}
