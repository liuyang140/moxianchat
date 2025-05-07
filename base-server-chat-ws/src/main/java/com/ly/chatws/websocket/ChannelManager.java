package com.ly.chatws.websocket;

import io.netty.channel.Channel;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ChannelManager {

    private static final Map<Long, Channel> USER_CHANNEL_MAP = new ConcurrentHashMap<>();
    private static final Map<Long, Long> LAST_ACTIVE_TIME_MAP = new ConcurrentHashMap<>();
    private static final Map<Long, Set<Long>> ROOM_USER_MAP = new ConcurrentHashMap<>();

    private static final Lock lock = new ReentrantLock();

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
        lock.lock();
        try {
            return new HashMap<>(USER_CHANNEL_MAP);  // 返回副本
        } finally {
            lock.unlock();
        }
    }

    public static void refreshLastActiveTime(Long customerId) {
        LAST_ACTIVE_TIME_MAP.put(customerId, System.currentTimeMillis());
    }

    public static Long getLastActiveTime(Long customerId) {
        return LAST_ACTIVE_TIME_MAP.get(customerId);
    }

    public static void add(Long userId, Channel channel) {
        lock.lock();
        try {
            USER_CHANNEL_MAP.put(userId, channel);
        } finally {
            lock.unlock();
        }
    }

    public static void remove(Channel channel) {
        USER_CHANNEL_MAP.entrySet().removeIf(entry -> entry.getValue().equals(channel));
    }

    public static Channel get(Long userId) {
        lock.lock();
        try {
            return USER_CHANNEL_MAP.get(userId);
        } finally {
            lock.unlock();
        }
    }

    public static boolean isOnline(Long userId) {
        return USER_CHANNEL_MAP.containsKey(userId);
    }
}