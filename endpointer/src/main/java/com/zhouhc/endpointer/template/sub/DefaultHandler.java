package com.zhouhc.endpointer.template.sub;

import com.zhouhc.endpointer.template.AbsHandler;

public class DefaultHandler extends AbsHandler {

    public DefaultHandler() {
        super("boray-default", "", "", "");
    }

    @Override
    public boolean isNeedAnalysis() {
        return false;
    }

    @Override
    public boolean isNeedStorage() {
        return false;
    }

    @Override
    public boolean isNeedPush() {
        return false;
    }

    @Override
    public String returnMsg() {
        return "请重新注册";
    }
}
