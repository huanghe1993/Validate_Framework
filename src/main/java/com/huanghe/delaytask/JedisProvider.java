package com.huanghe.delaytask;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

@Component
public class JedisProvider implements InitializingBean {

    private JedisPool jedisPool;

    @Override
    public void afterPropertiesSet() throws Exception {
        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
        jedisPool = new JedisPool(jedisPoolConfig, "localhost");
    }

    public Jedis provide(){
        return jedisPool.getResource();
    }
}
