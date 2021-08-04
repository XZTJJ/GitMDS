package com.zhouhc.endpointer.template.sub;

import com.zhouhc.endpointer.template.AbsHandler;

public class CreateHandler extends AbsHandler {

    public CreateHandler(String streamName, String sourceDataType, String sourceDataSchema, String pushIP) {
        super(streamName, sourceDataType, sourceDataSchema, pushIP);
    }

    public CreateHandler(String streamName, String sourceDataType, String sourceDataSchema, String storeType, String pushIP, String pushDataType, String pushDataSchema) {
        super(streamName, sourceDataType, sourceDataSchema, storeType, pushIP, pushDataType, pushDataSchema);
    }

    @Override
    public String returnMsg() {
        return "创建成功," + super.getStreamName();
    }
}
