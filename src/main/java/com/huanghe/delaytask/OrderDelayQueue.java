package com.huanghe.delaytask;

import java.util.List;

/**
 * 延迟队列接口
 */
public interface OrderDelayQueue {

    void enqueue(OrderMessage message);

    List<OrderMessage> dequeue(String min, String max, String offset, String limit);

    List<OrderMessage> dequeue();

    String enqueueSha();

    String dequeueSha();
}
