package com.huanghe.delaytask;

import com.alibaba.fastjson.JSON;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;
import redis.clients.jedis.Jedis;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

@RequiredArgsConstructor
@Component
public class RedisOrderDelayQueue implements OrderDelayQueue, InitializingBean {

    private static final String MIN_SCORE = "0";
    private static final String OFFSET = "0";
    private static final String LIMIT = "10";
    private static final String ORDER_QUEUE = "ORDER_QUEUE";
    private static final String ORDER_DETAIL_QUEUE = "ORDER_DETAIL_QUEUE";
    private static final String ENQUEUE_LUA_SCRIPT_LOCATION = "/lua/enqueue.lua";
    private static final String DEQUEUE_LUA_SCRIPT_LOCATION = "/lua/dequeue.lua";
    private static final AtomicReference<String> ENQUEUE_LUA_SHA = new AtomicReference<>();
    private static final AtomicReference<String> DEQUEUE_LUA_SHA = new AtomicReference<>();
    private static final List<String> KEYS = new ArrayList<>();

    private final JedisProvider jedisProvider;

    static {
        KEYS.add(ORDER_QUEUE);
        KEYS.add(ORDER_DETAIL_QUEUE);
    }

    @Override
    public void enqueue(OrderMessage message) {
        List<String> args = new ArrayList<>();
        args.add(message.getOrderId());
        args.add(String.valueOf(System.currentTimeMillis()));
        args.add(message.getOrderId());
        args.add(JSON.toJSONString(message));
        try (Jedis jedis = jedisProvider.provide()) {
            jedis.evalsha(ENQUEUE_LUA_SHA.get(), KEYS, args);
        }
    }

    @Override
    public List<OrderMessage> dequeue() {
        // 30分钟之前
        String maxScore = String.valueOf(System.currentTimeMillis() - 30 * 60 * 1000);
        return dequeue(MIN_SCORE, maxScore, OFFSET, LIMIT);
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<OrderMessage> dequeue(String min, String max, String offset, String limit) {
        List<String> args = new ArrayList<>();
        args.add(max);
        args.add(min);
        args.add(offset);
        args.add(limit);
        List<OrderMessage> result =  new ArrayList<>();
        try (Jedis jedis = jedisProvider.provide()) {
            List<String> eval = (List<String>) jedis.evalsha(DEQUEUE_LUA_SHA.get(), KEYS, args);
            if (null != eval) {
                for (String e : eval) {
                    result.add(JSON.parseObject(e, OrderMessage.class));
                }
            }
        }
        return result;
    }

    @Override
    public String enqueueSha() {
        return ENQUEUE_LUA_SHA.get();
    }

    @Override
    public String dequeueSha() {
        return DEQUEUE_LUA_SHA.get();
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        // 加载Lua脚本
        loadLuaScript();
    }

    private void loadLuaScript() throws Exception {
        try (Jedis jedis = jedisProvider.provide()) {
            ClassPathResource resource = new ClassPathResource(ENQUEUE_LUA_SCRIPT_LOCATION);
            String luaContent = StreamUtils.copyToString(resource.getInputStream(), StandardCharsets.UTF_8);
            String sha = jedis.scriptLoad(luaContent);
            ENQUEUE_LUA_SHA.compareAndSet(null, sha);
            resource = new ClassPathResource(DEQUEUE_LUA_SCRIPT_LOCATION);
            luaContent = StreamUtils.copyToString(resource.getInputStream(), StandardCharsets.UTF_8);
            sha = jedis.scriptLoad(luaContent);
            DEQUEUE_LUA_SHA.compareAndSet(null, sha);
        }
    }

    public static void main(String[] as) throws Exception {
        DateTimeFormatter f = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
        JedisProvider jedisProvider = new JedisProvider();
        jedisProvider.afterPropertiesSet();
        RedisOrderDelayQueue queue = new RedisOrderDelayQueue(jedisProvider);
        queue.afterPropertiesSet();
        // 写入测试数据
        OrderMessage message = new OrderMessage();
        message.setAmount(BigDecimal.valueOf(10086));
        message.setOrderId("ORDER_ID_10086");
        message.setUserId(10086L);
        message.setTimestamp(LocalDateTime.now().format(f));
        List<String> args =  new ArrayList<>();
        args.add(message.getOrderId());
        // 测试需要,score设置为30分钟之前
        args.add(String.valueOf(System.currentTimeMillis() - 30 * 60 * 1000));
        args.add(message.getOrderId());
        args.add(JSON.toJSONString(message));
        try (Jedis jedis = jedisProvider.provide()) {
            jedis.evalsha(ENQUEUE_LUA_SHA.get(), KEYS, args);
        }
        List<OrderMessage> dequeue = queue.dequeue();
        System.out.println(dequeue);
    }
}
