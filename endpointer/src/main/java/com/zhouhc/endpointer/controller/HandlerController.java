package com.zhouhc.endpointer.controller;

import com.zhouhc.endpointer.template.AbsHandler;
import com.zhouhc.endpointer.utils.MsgUtils;
import com.zhouhc.endpointer.utils.PipelineUitl;
import org.springframework.web.bind.annotation.*;

/**
 * stream 处理 类
 */
@RestController
@RequestMapping("/streams")
public class HandlerController {

    //处理某个stream的请求数据
    @PostMapping("/hander/{streamName}")
    public String handlerStream(@PathVariable String streamName, @RequestBody String data) throws Exception {
        AbsHandler hander = PipelineUitl.getHandlerByStreamName(streamName);
        hander.doHandler(data);
        return MsgUtils.successMsg();
    }

}
