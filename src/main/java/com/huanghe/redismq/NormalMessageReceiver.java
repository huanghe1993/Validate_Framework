package com.huanghe.redismq;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class NormalMessageReceiver implements MsgConsumer {
    private static Logger log = LoggerFactory.getLogger(NormalMessageReceiver.class);

    @Override
    public void onMessage(Object message) {
        log.info("收到消息:" + message);
    }

    @Override
    public void onError(Object msg, Exception e) {
        log.error("发生错误,消息:{}", msg, e);
    }
}
