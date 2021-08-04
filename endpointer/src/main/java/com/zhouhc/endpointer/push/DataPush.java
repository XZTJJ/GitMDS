package com.zhouhc.endpointer.push;

import com.zhouhc.endpointer.template.AbsHandler;

//推送数据，
public interface DataPush {

    //平装数据，只返回需要的数据
    public String getOnlyNeedJsonStr(AbsHandler handler, String storeData);

    //推送数据
    public String pushMessage(String streamName, String needMessage);
}
