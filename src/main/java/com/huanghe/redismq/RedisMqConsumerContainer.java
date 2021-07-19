package com.huanghe.redismq;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Redis消息队列的 消息--消费者容器
 */
public class RedisMqConsumerContainer {
    private static final Logger log = LoggerFactory.getLogger(RedisMqConsumerContainer.class);

    /**
     * Key是队列的名称，value是消费者
     */
    private Map<String, QueueConfiguration> consumerMap = new HashMap<>();

    /**
     * redisTemplate
     */
    private RedisTemplate<String, Object> redisTemplate;

    static boolean run;

    private ExecutorService exec;

    public RedisMqConsumerContainer(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    /**
     * 添加Consumer到容器中
     * @param configuration 配置类
     */
    public void addConsumer(QueueConfiguration configuration) {
        if (consumerMap.containsKey(configuration.getQueue())) {
            log.warn("Key:{} this key already exists, and it will be replaced", configuration.getQueue());
        }
        if (configuration.getConsumer() == null) {
            log.warn("Key:{} consumer cannot be null, this configuration will be skipped", configuration.getQueue());
        }
        consumerMap.put(configuration.getQueue(), configuration);
    }

    public void destroy() {
        run = false;
        this.exec.shutdown();
        log.info("QueueListener exiting.");
        while (!this.exec.isTerminated()) {

        }
        log.info("QueueListener exited.");
    }

    /**
     * 容器在启动的时候会进行初始化
     */
    public void init() {
        run = true;
        this.exec = Executors.newCachedThreadPool(r -> {
            final AtomicInteger threadNumber = new AtomicInteger(1);
            return new Thread(r, "RedisMQListener-" + threadNumber.getAndIncrement());
        });
        consumerMap = Collections.unmodifiableMap(consumerMap);
        consumerMap.forEach((k, v) -> exec.submit(new QueueListener(redisTemplate, v.getQueue(), v.getConsumer())));
    }


}
