package com.zhouhc.endpointer.controller;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.zhouhc.endpointer.error.CustomException;
import com.zhouhc.endpointer.template.AbsHandler;
import com.zhouhc.endpointer.utils.ConstantUtil;
import com.zhouhc.endpointer.utils.JSONUtil;
import com.zhouhc.endpointer.utils.MsgUtils;
import com.zhouhc.endpointer.utils.PipelineUitl;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import static com.zhouhc.endpointer.renum.ErrorEnum.WEBSTARTERROR;

/**
 * stream 注册 和 非注册 类
 */
@RestController
@RequestMapping("/streams/")
public class RegisterController {

    //登录接口
    @PostMapping("/register")
    public String register(@RequestBody String requestJson) {
        //判断是否启动成功
        if (StringUtils.isBlank(ConstantUtil.HostAddr))
            throw new CustomException(WEBSTARTERROR, null);
        AbsHandler absHandler = PipelineUitl.addHandler(JSONUtil.toJson(requestJson));
        //生成登录和登出接口
        JsonObject postInterface = JSONUtil.getJsonFromArrays("url", "http://" + ConstantUtil.HostAddr + ":" + ConstantUtil.Post + "/streams/hander/" + absHandler.getStreamName(),
                "HTTPType", "POST, 数据放在请求体中", "msg", "传输数据的接口");
        JsonObject deleteInterface = JSONUtil.getJsonFromArrays("url", "http://" + ConstantUtil.HostAddr + ":" + ConstantUtil.Post + "/streams/unregister/" + absHandler.getStreamName(),
                "HttpType", "Delete", "msg", "注销stream的接口");
        //数据返回
        JsonArray array = new JsonArray();
        array.add(postInterface);
        array.add(deleteInterface);
        return MsgUtils.successMsg(array);
    }

    @DeleteMapping("/unregister/{streamName}")
    public String unRegister(@PathVariable String streamName) {
        AbsHandler absHandler = PipelineUitl.deleteByStreanName(streamName);
        if (absHandler != null)
            return MsgUtils.successMsg(String.format("删除%s成功", absHandler.getStreamName()));
        else
            return MsgUtils.OtherMsg(500, String.format("该%s不存在", streamName));
    }

}
