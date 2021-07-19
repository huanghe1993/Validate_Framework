package com.huanghe.redismq;

/**
 * 消息-消费者容器
 * 配置类
 */
public class QueueConfiguration {
    /**
     * 队列名称
     */
    private String queue;
    /**
     * 消费者
     */
    private MsgConsumer consumer;

    private QueueConfiguration() {
    }

    public static Builder builder() {
        return new Builder();
    }

    String getQueue() {
        return queue;
    }

    MsgConsumer getConsumer() {
        return consumer;
    }

    public static class Builder {
        private QueueConfiguration configuration = new QueueConfiguration();

        public QueueConfiguration defaultConfiguration(MsgConsumer consumer) {
            configuration.consumer = consumer;
            configuration.queue = consumer.getClass().getSimpleName();
            return configuration;
        }

        public Builder queue(String queue) {
            configuration.queue = queue;
            return this;
        }

        public Builder consumer(MsgConsumer consumer) {
            configuration.consumer = consumer;
            return this;
        }

        public QueueConfiguration build() {
            if (configuration.queue == null || configuration.queue.length() == 0) {
                if (configuration.consumer != null) {
                    configuration.queue = configuration.getClass().getSimpleName();
                }
            }
            return configuration;
        }

    }
}
