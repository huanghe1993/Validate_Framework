package com.huanghe.delaytask;

import com.google.common.collect.Lists;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.time.StopWatch;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

@DisallowConcurrentExecution
@Component
public class OrderMessageConsumer implements Job {

    private static final AtomicInteger COUNTER = new AtomicInteger();
    private static final ExecutorService BUSINESS_WORKER_POOL = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors(), r -> {
        Thread thread = new Thread(r);
        thread.setDaemon(true);
        thread.setName("OrderMessageConsumerWorker-" + COUNTER.getAndIncrement());
        return thread;
    });
    private static final Logger LOGGER = LoggerFactory.getLogger(OrderMessageConsumer.class);

    @Autowired
    private OrderDelayQueue orderDelayQueue;

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        LOGGER.info("订单消息处理定时任务开始执行......");
        List<OrderMessage> messages = orderDelayQueue.dequeue();
        if (!messages.isEmpty()) {
            // 简单的列表等分放到线程池中执行
            List<List<OrderMessage>> partition = Lists.partition(messages, 2);
            int size = partition.size();
            final CountDownLatch latch = new CountDownLatch(size);
            for (List<OrderMessage> p : partition) {
                BUSINESS_WORKER_POOL.execute(new ConsumeTask(p, latch));
            }
            try {
                latch.await();
            } catch (InterruptedException ignore) {
                //ignore
            }
        }
        stopWatch.stop();
//        LOGGER.info("订单消息处理定时任务执行完毕,耗时:{} ms......", stopWatch.getTotalTimeMillis());
    }

    @RequiredArgsConstructor
    private static class ConsumeTask implements Runnable {

        private final List<OrderMessage> messages;
        private final CountDownLatch latch;

        @Override
        public void run() {
            try {
                // 实际上这里应该单条捕获异常
                for (OrderMessage message : messages) {
                    LOGGER.info("处理订单信息,内容:{}", message);
                }
            } finally {
                latch.countDown();
            }
        }
    }
}
