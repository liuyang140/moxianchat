package com.ly.common.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class RedisUtils {

    @Autowired
    private StringRedisTemplate redisTemplate;

    /**
     * 设置 key 的字符串值，并可指定过期时间（单位：秒）
     */
    public void set(String key, String value, long timeoutSeconds) {
        redisTemplate.opsForValue().set(key, value, timeoutSeconds, TimeUnit.SECONDS);
    }

    /**
     * 设置 key 的字符串值（不过期）
     */
    public void set(String key, String value) {
        redisTemplate.opsForValue().set(key, value);
    }

    /**
     * 获取指定 key 的字符串值
     */
    public String get(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    /**
     * 删除指定 key
     */
    public void delete(String key) {
        redisTemplate.delete(key);
    }

    /**
     * 判断 key 是否存在
     */
    public boolean hasKey(String key) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }

    /**
     * 设置 key 的过期时间（单位：秒）
     */
    public void expire(String key, long timeoutSeconds) {
        redisTemplate.expire(key, timeoutSeconds, TimeUnit.SECONDS);
    }

    /**
     * 获取 key 剩余过期时间（单位：秒）
     */
    public Long getExpire(String key) {
        return redisTemplate.getExpire(key, TimeUnit.SECONDS);
    }
}