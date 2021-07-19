package com.huanghe.delaytask;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class JedisPoolUtils {
    private JedisPoolUtils() {
    }

    private static JedisPool jedisPool;

    public static Jedis provide(){
        JedisPool jedisPool = getInstance();
        return jedisPool.getResource();
    }

    public static JedisPool getInstance() {
        if (jedisPool == null) {
            synchronized (JedisPoolUtils.class) {
                if (jedisPool == null) {
                    JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
                    jedisPoolConfig.setMaxTotal(20);
                    jedisPoolConfig.setMaxIdle(10);
                    jedisPool = new JedisPool(jedisPoolConfig, "127.0.0.1", 6379);
                }
            }
        }
        return jedisPool;
    }

    public static void main(String[] args) {
        System.out.println(111);
    }

}
