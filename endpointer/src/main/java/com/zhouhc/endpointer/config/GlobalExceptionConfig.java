package com.zhouhc.endpointer.config;

import com.zhouhc.endpointer.error.CustomException;
import com.zhouhc.endpointer.renum.ErrorEnum;
import com.zhouhc.endpointer.utils.MsgUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;


//全局错误处理 配置 中心
@ControllerAdvice
public class GlobalExceptionConfig {

    private static final Logger LOGGER = LoggerFactory.getLogger(GlobalExceptionConfig.class);

    @ExceptionHandler(value = Exception.class)
    @ResponseBody
    public String allExceptionHandler(Exception exception) throws Exception {
        LOGGER.error("系统运行错误", exception);
        int code = 0;
        String msg = "";
        if (exception instanceof CustomException) {
            ErrorEnum errorEnum = ((CustomException) exception).getErrorEnum();
            code = errorEnum.getCode();
            msg = errorEnum.getMsg();
        } else {
            code = 500;
            msg = exception.getMessage();
        }
        return MsgUtils.OtherMsg(code, msg);
    }
}
