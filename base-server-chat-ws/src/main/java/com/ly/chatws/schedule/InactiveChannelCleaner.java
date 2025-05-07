package com.ly.chatws.schedule;

import com.ly.chatws.websocket.ChannelManager;
import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@Slf4j
public class InactiveChannelCleaner {

    @Value("${netty.websocket.ping-interval}")
    private int pingInterval;

    // 每10秒执行一次
    @Scheduled(fixedDelay = 10000)
    public void cleanInactiveChannels() {
        long now = System.currentTimeMillis();
        long timeout = pingInterval * 1000; // 30秒未心跳视为断线

        for (Map.Entry<Long, Channel> entry : ChannelManager.getAll().entrySet()) {
            Long userId = entry.getKey();
            Channel channel = entry.getValue();
            Long lastActive = ChannelManager.getLastActiveTime(userId);

            if (lastActive == null || now - lastActive > timeout) {
                log.warn("用户 {} 超过 {} 秒未心跳，关闭连接", userId, timeout / 1000);
                channel.close();
                ChannelManager.remove(channel);
            }
        }
    }
}