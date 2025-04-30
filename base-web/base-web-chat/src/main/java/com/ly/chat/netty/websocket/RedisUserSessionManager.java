package com.ly.chat.netty.websocket;

import com.ly.common.util.RedisUtils;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.concurrent.TimeUnit;

import static com.ly.common.constant.RedisConstant.ONLINE_KEY_PREFIX;

@Component
public class RedisUserSessionManager {

    @Resource
    private RedisUtils redisUtils;

    @Value("${spring.application.name:web-chat}")
    private String instanceId;

    /**
     * 用户上线（写入 Redis）
     */
    public void online(Long userId) {
        String key = ONLINE_KEY_PREFIX + userId;
        redisUtils.set(key, instanceId, 300); // 5 分钟过期
    }

    /**
     * 用户下线（删除 Redis）
     */
    public void offline(Long userId) {
        String key = ONLINE_KEY_PREFIX + userId;
        redisUtils.delete(key);
    }

    /**
     * 判断用户是否在线（Redis中存在）
     */
    public boolean isOnline(Long userId) {
        String key = ONLINE_KEY_PREFIX + userId;
        return Boolean.TRUE.equals(redisUtils.hasKey(key));
    }

    /**
     * 获取在线用户的服务实例 ID
     */
    public String getOnlineInstance(Long userId) {
        String key = ONLINE_KEY_PREFIX + userId;
        return redisUtils.get(key);
    }
}