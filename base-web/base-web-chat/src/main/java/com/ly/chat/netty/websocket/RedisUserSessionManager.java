package com.ly.chat.netty.websocket;

import com.ly.common.util.RedisUtils;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.ly.common.constant.RedisConstant.*;

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
        this.refreshAllTTL(userId); //刷新redis所有关联ttl
    }

    /**
     * 用户下线（删除 Redis）
     */
    public void offline(Long userId) {
        redisUtils.delete(ONLINE_KEY_PREFIX + userId);
        redisUtils.delete(CHANNEL_KEY_PREFIX + userId);
        redisUtils.delete(LAST_ACTIVE_PREFIX + userId);
    }

    /**
     * 判断用户是否在线（Redis中存在）
     */
    public boolean isOnline(Long userId) {
        return Boolean.TRUE.equals(redisUtils.hasKey(ONLINE_KEY_PREFIX + userId));
    }

    /**
     * 获取在线用户的服务实例 ID
     */
    public String getOnlineInstance(Long userId) {
        return redisUtils.get(ONLINE_KEY_PREFIX + userId);
    }


    /** 绑定用户对应的 Netty ChannelId */
    public void bindChannel(Long userId, String channelId) {
        redisUtils.set(CHANNEL_KEY_PREFIX + userId, channelId, 300); // 与上线时间一致
        refreshAllTTL(userId); // 自动刷新所有TTL
    }

    /** 获取用户绑定的 ChannelId */
    public String getChannelId(Long userId) {
        return redisUtils.get(CHANNEL_KEY_PREFIX + userId);
    }

    /** 加入房间：roomId -> Set<userId> */
    public void joinRoom(Long roomId, Long userId) {
        String roomKey = ROOM_KEY_PREFIX + roomId;
        String userKey = USER_ROOM_PREFIX + userId;
        redisUtils.sAdd(roomKey, userId.toString());
        redisUtils.sAdd(userKey, roomId.toString());

        redisUtils.expire(roomKey, 1, TimeUnit.HOURS);
        redisUtils.expire(userKey, 1, TimeUnit.HOURS);
    }

    /** 离开房间 */
    public void leaveRoom(Long roomId, Long userId) {
        redisUtils.sRemove(ROOM_KEY_PREFIX + roomId, userId.toString());
        redisUtils.sRemove(USER_ROOM_PREFIX + userId, roomId.toString());
    }

    /** 获取房间内所有用户（不判断在线状态） */
    public Set<String> getRoomUsers(Long roomId) {
        return redisUtils.sMembers(ROOM_KEY_PREFIX + roomId);
    }

    /**
     * 获取房间内所有在线用户ID
     */
    public Set<String> getOnlineRoomUsers(Long roomId) {
        Set<String> allUsers = redisUtils.sMembers(ROOM_KEY_PREFIX + roomId);
        if (allUsers == null || allUsers.isEmpty()) {
            return Set.of();
        }

        return allUsers.stream()
                .filter(userId -> isOnline(Long.valueOf(userId)))
                .collect(Collectors.toSet());
    }

    /** 获取用户所在所有房间 */
    public Set<String> getUserRooms(Long userId) {
        return redisUtils.sMembers(USER_ROOM_PREFIX + userId);
    }

    /** 更新用户活跃时间 */
    public void updateLastActiveTime(Long userId, long timestamp) {
        redisUtils.set(LAST_ACTIVE_PREFIX + userId, String.valueOf(timestamp), 300);
    }

    /** 获取用户上次活跃时间 */
    public Long getLastActiveTime(Long userId) {
        String val = redisUtils.get(LAST_ACTIVE_PREFIX + userId);
        return val == null ? null : Long.valueOf(val);
    }

    /** 用户断开连接，清理所有关联数据 */
    public void clearUserSession(Long userId) {
        offline(userId);
        // TODO 移除房间关系，可遍历 roomId（存 userId->Set<roomId> 关系做反向索引）
        Set<String> roomIds = redisUtils.sMembers(USER_ROOM_PREFIX + userId);
        if (roomIds != null) {
            for (String roomId : roomIds) {
                leaveRoom(Long.valueOf(roomId), userId);
            }
        }

        redisUtils.delete(USER_ROOM_PREFIX + userId);
    }

    /** 是否在房间中*/
    public boolean isUserInRoom(Long roomId, Long userId) {
        return redisUtils.isMember(ROOM_KEY_PREFIX + roomId, userId.toString());
    }

    /**刷新在线状态*/
    public void refreshAllTTL(Long userId) {
        int ttl = 300; // 5分钟
        TimeUnit unit = TimeUnit.SECONDS;

        // 1. 刷新用户相关key的TTL
        redisUtils.expire(ONLINE_KEY_PREFIX + userId, ttl, unit);
        redisUtils.expire(CHANNEL_KEY_PREFIX + userId, ttl, unit);
        redisUtils.expire(LAST_ACTIVE_PREFIX + userId, ttl, unit);
        redisUtils.expire(USER_ROOM_PREFIX + userId, ttl, unit);

        // 2. 刷新用户所在所有房间的room -> userId集合的TTL
        Set<String> roomIds = redisUtils.sMembers(USER_ROOM_PREFIX + userId);
        if (roomIds != null) {
            for (String roomId : roomIds) {
                redisUtils.expire(ROOM_KEY_PREFIX + roomId, ttl, unit);
            }
        }
    }

    public void saveRoomUser(Long roomId, Long userId) {
        redisUtils.sAdd(ROOM_KEY_PREFIX+ roomId, String.valueOf(userId));
        redisUtils.sAdd(USER_ROOM_PREFIX + userId, String.valueOf(roomId));
        refreshAllTTL(userId);
    }



}