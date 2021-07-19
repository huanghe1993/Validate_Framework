package com.huanghe.redismq;

/**
 * https://www.codeleading.com/article/86594507120/
 */
public interface MsgConsumer {
    void onMessage(Object message);

    void onError(Object msg, Exception e);
}
