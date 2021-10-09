package com.zhouhc.template;

import com.zhouhc.error.CustomExcetion;

import java.util.concurrent.Callable;

/**
 * 实现Callable, 添加一些额外的逻辑进行处理，比如登录和登出等
 * 建议使用这种方式
 */
public abstract class ReCommand implements Callable<Integer> {
    /**
     * 模板方法
     */
    @Override
    public Integer call() throws Exception {
        try {
            Integer status = this.dotask();
            return status;
        } catch (Exception e) {
            if (e instanceof CustomExcetion)
                throw (CustomExcetion) e;
            else
                throw new CustomExcetion(500, e.getMessage(), e);
        }
    }


    /**
     * 具体的任务处理方法, 就是原来run中的逻辑
     */
    public abstract Integer dotask() throws Exception;

}
