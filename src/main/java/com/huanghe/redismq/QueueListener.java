package com.huanghe.redismq;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.QueryTimeoutException;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.concurrent.TimeUnit;

/**
 * 消息监听类
 */
public class QueueListener implements Runnable {
    public static final Logger log = LoggerFactory.getLogger(QueueListener.class);
    private RedisTemplate<String, Object> redisTemplate;
    private String queue;
    private MsgConsumer consumer;

    /**
     * 消息的监听器
     * @param redisTemplate redisTemplate
     * @param queue 队列名称
     * @param consumer 消费者
     */
    public QueueListener(RedisTemplate<String, Object> redisTemplate, String queue, MsgConsumer consumer) {
        this.redisTemplate = redisTemplate;
        this.queue = queue;
        this.consumer = consumer;
    }

    @Override
    public void run() {
        log.info("QueueListener start...queue:{}", queue);
        while (RedisMqConsumerContainer.run) {
            try {
                // 自定义队列名称,最长阻塞等待时间,时间单位
                Object msg = redisTemplate.opsForList().rightPop(queue, 30, TimeUnit.SECONDS);
                if (msg != null) {
                    try {
                        consumer.onMessage(msg);
                    } catch (Exception e) {
                        consumer.onError(msg, e);
                    }
                }
            } catch (QueryTimeoutException ignored) {
            } catch (Exception e) {
                if (RedisMqConsumerContainer.run) {
                    log.error("Queue:{}", queue, e);
                } else {
                    log.info("QueueListener exits...queue:{}", queue);
                }
            }
        }
    }
}
