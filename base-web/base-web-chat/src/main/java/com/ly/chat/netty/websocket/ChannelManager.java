package com.ly.chat.netty.websocket;

import io.netty.channel.Channel;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class ChannelManager {

    private static final Map<Long, Channel> USER_CHANNEL_MAP = new ConcurrentHashMap<>();
    private static final Map<Long, Long> lastActiveTimeMap = new ConcurrentHashMap<>();
    private static final Map<Long, Set<Long>> ROOM_USER_MAP = new ConcurrentHashMap<>();

    public static void joinRoom(Long roomId, Long userId) {
        ROOM_USER_MAP.computeIfAbsent(roomId, k -> ConcurrentHashMap.newKeySet()).add(userId);
    }

    public static void leaveRoom(Long roomId, Long userId) {
        Set<Long> users = ROOM_USER_MAP.get(roomId);
        if (users != null) {
            users.remove(userId);
            if (users.isEmpty()) {
                ROOM_USER_MAP.remove(roomId);
            }
        }
    }

    public static Set<Long> getRoomUsers(Long roomId) {
        return ROOM_USER_MAP.getOrDefault(roomId, Collections.emptySet());
    }
    public static Map<Long, Channel> getAll() {
        return USER_CHANNEL_MAP;
    }

    public static void refreshLastActiveTime(Long customerId) {
        lastActiveTimeMap.put(customerId, System.currentTimeMillis());
    }

    public static Long getLastActiveTime(Long customerId) {
        return lastActiveTimeMap.get(customerId);
    }

    public static void add(Long userId, Channel channel) {
        USER_CHANNEL_MAP.put(userId, channel);
    }

    public static void remove(Channel channel) {
        USER_CHANNEL_MAP.entrySet().removeIf(entry -> entry.getValue().equals(channel));
    }

    public static Channel get(Long userId) {
        return USER_CHANNEL_MAP.get(userId);
    }

    public static boolean isOnline(Long userId) {
        return USER_CHANNEL_MAP.containsKey(userId);
    }
}