package com.ly.common.util;

import com.alibaba.fastjson.JSON;
import com.ly.common.constant.RedisConstant;
import com.ly.model.dto.user.CacheUserDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class UserCacheUtils {

    // 默认缓存时间：7天（单位：秒）
    private static final long DEFAULT_EXPIRE_SECONDS = 3600 * 24 * 1;

    @Autowired
    private RedisUtils redisUtils;

    /**
     * 缓存用户信息（使用默认过期时间）
     */
    public void cacheUser(Long userId, CacheUserDTO userDTO) {
        cacheUser(userId, userDTO, DEFAULT_EXPIRE_SECONDS);
    }

    /**
     * 缓存用户信息（自定义过期时间）
     */
    public void cacheUser(Long userId, CacheUserDTO userDTO, long timeoutSeconds) {
        String key = RedisConstant.USER_LOGIN_KEY_PREFIX + userId;
        String value = JSON.toJSONString(userDTO);
        redisUtils.set(key, value, timeoutSeconds);
    }

    /**
     * 获取用户缓存信息（返回 CacheUserDTO）
     */
    public CacheUserDTO getUser(Long userId) {
        String key = RedisConstant.USER_LOGIN_KEY_PREFIX + userId;
        String json = redisUtils.get(key);
        if (json == null) return null;
        return JSON.parseObject(json, CacheUserDTO.class);
    }

    /**
     * 更新用户缓存的过期时间（延长有效期）
     */
    public void refreshUserExpire(Long userId) {
        String key = RedisConstant.USER_LOGIN_KEY_PREFIX + userId;
        redisUtils.expire(key, DEFAULT_EXPIRE_SECONDS);
    }
}