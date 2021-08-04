package com.zhouhc.ssewesocket.controller;

import com.zhouhc.ssewesocket.task.ClientMsgCallback;
import com.zhouhc.ssewesocket.task.RedisSubTask;
import com.zhouhc.ssewesocket.thread.GlobalThreadPool;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

//测试功能
@RestController
@RequestMapping("/insert")
public class TestController {

    @PostMapping("/datatest")
    public Map subMessage(@RequestBody String key) {
        for (int i = 0; i < 3; i++)
            GlobalThreadPool.exec(new RedisSubTask(key), new ClientMsgCallback());
        Map map = new HashMap();
        map.put("code", 200);
        map.put("msg", "success");
        return map;
    }
}
