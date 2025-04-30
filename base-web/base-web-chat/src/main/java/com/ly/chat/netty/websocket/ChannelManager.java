package com.ly.chat.netty.websocket;

import io.netty.channel.Channel;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ChannelManager {

    private static final Map<Long, Channel> USER_CHANNEL_MAP = new ConcurrentHashMap<>();

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