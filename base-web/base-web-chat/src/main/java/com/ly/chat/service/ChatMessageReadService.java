package com.ly.chat.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ly.model.entity.chat.ChatMessageRead;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * 用户对聊天室已读状态的 Service 接口
 */
public interface ChatMessageReadService extends IService<ChatMessageRead> {

    Map<Long, Integer> getUnreadMessageCountByRoomId(Long roomId);

    Long getLatestUnreadMessageId(Long roomId, Long userId);

    /**
     * 更新用户在聊天室的已读状态
     */
    Boolean updateReadStatus(Long roomId, Long userId, LocalDateTime lastReadTime);

    /**
     * 查询指定用户在指定房间的未读数量
     */
    Long countUnreadMessages(Long roomId, Long userId);

    /**
     * 更新用户在房间中的最后已读时间
     */
    void markAsRead(Long roomId, Long userId);

    Long getTotalUnreadCount(Long userId);
}

