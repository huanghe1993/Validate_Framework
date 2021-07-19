package com.huanghe.redismq;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

/**
 * 消息-生产者
 */
@Component
public class QueueSender {
    private RedisTemplate<String, Object> redisTemplate;

    public QueueSender(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void sendMsg(String queue, Object msg) {
        redisTemplate.opsForList().leftPush(queue, msg);
    }
}
