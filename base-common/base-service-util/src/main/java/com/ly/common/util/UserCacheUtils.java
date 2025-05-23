package com.ly.common.util;

import com.alibaba.fastjson.JSON;
import com.ly.common.constant.RedisConstant;
import com.ly.model.dto.user.CacheUserDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UserCacheUtils {

    // 默认缓存时间：7天（单位：秒）
    private static final long DEFAULT_EXPIRE_SECONDS = 3600 * 24 * 1;

    @Autowired
    private RedisUtils redisUtils;

    /**
     * 缓存用户信息（使用默认过期时间）
     */
    public void cacheLoginUser(Long userId, CacheUserDTO userDTO) {
        cacheLoginUser(userId, userDTO, DEFAULT_EXPIRE_SECONDS);
    }

    /**
     * 缓存用户信息（自定义过期时间）
     */
    public void cacheLoginUser(Long userId, CacheUserDTO userDTO, long timeoutSeconds) {
        String key = RedisConstant.USER_LOGIN_KEY_PREFIX + userId;
        String value = JSON.toJSONString(userDTO);
        redisUtils.set(key, value, timeoutSeconds);
    }

    /**
     * 获取用户缓存信息（返回 CacheUserDTO）
     */

    public CacheUserDTO getCacheLoginUser() {
        return this.getCacheLoginUser(this.getLoginUserId());
    }

    public CacheUserDTO getCacheLoginUser(Long userId) {
        String key = RedisConstant.USER_LOGIN_KEY_PREFIX + userId;
        String json = redisUtils.get(key);
        if (json == null) return null;
        return JSON.parseObject(json, CacheUserDTO.class);
    }

    /**
     * 更新用户缓存的过期时间（延长有效期）
     */
    public void refreshLoginUserExpire(Long userId) {
        String key = RedisConstant.USER_LOGIN_KEY_PREFIX + userId;
        redisUtils.expire(key, DEFAULT_EXPIRE_SECONDS);
    }

    /*
    * 获取用户id
    * */
     public Long getLoginUserId() {
        return AuthContextHolder.getUserId();
    }

    /*
    * 自定义缓存用户信息
    * */
    public void cacheUserInfo(Long userId, CacheUserDTO userDTO,  long timeoutSeconds) {
        String key = RedisConstant.USER_CACHE_KEY_PREFIX + userId;
        String value = JSON.toJSONString(userDTO);
        redisUtils.set(key, value, timeoutSeconds);
    }

    /**
     * 自定义缓存用户信息（使用默认过期时间）
     */
    public void cacheUserInfo(Long userId, CacheUserDTO userDTO) {
        cacheLoginUser(userId, userDTO, DEFAULT_EXPIRE_SECONDS);
    }

    /*
    * 获取自定义缓存用户信息
    * */
    public CacheUserDTO getCacheUserInfo(Long userId) {
        String key = RedisConstant.USER_CACHE_KEY_PREFIX + userId;
        String json = redisUtils.get(key);
        if (json == null) return null;
        return JSON.parseObject(json, CacheUserDTO.class);
    }


}