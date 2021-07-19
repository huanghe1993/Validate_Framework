package com.huanghe.redismq;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

//开启定时器功能
@EnableScheduling
@Component
public class MessageSenderTest {
    @Autowired
    private QueueSender queueSender;

    @Scheduled(fixedDelay = 5000)
    public void sendMessageXXX() {
        //stringRedisTemplate.convertAndSend("chat1", String.valueOf(Math.random()));
        //producer.publishMessageXXX("队列消息1xxxxx"+System.currentTimeMillis());

        for (int i = 0; i < 20; i++) {
            queueSender.sendMsg("TEST0","hello quit~~~~,序号:"+i);
            queueSender.sendMsg("normalMessage","hello quit~~~~,序号:"+i);
            queueSender.sendMsg("TEST2","hello quit~~~~,序号:"+i);
        }

    }

}
