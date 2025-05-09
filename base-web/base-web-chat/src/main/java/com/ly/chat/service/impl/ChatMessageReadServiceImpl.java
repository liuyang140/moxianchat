package com.ly.chat.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ly.chat.mapper.ChatMessageReadMapper;
import com.ly.chat.service.ChatMessageReadService;
import com.ly.model.entity.chat.ChatMessageRead;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 用户对聊天室已读状态的 Service 实现类
 */
@Service
public class ChatMessageReadServiceImpl extends ServiceImpl<ChatMessageReadMapper, ChatMessageRead> implements ChatMessageReadService {

    @Override
    public Map<Long, Integer> getUnreadMessageCountByRoomId(Long roomId) {
        List<Map<String, Object>> list = baseMapper.getUnreadMessageCountByRoomId(roomId);
        return list.stream().collect(Collectors.toMap(
                m -> ((Number)m.get("user_id")).longValue(),
                m -> ((Number)m.get("unread_count")).intValue()
        ));
    }

    @Override
    public Long getLatestUnreadMessageId(Long roomId, Long userId) {
        return baseMapper.getLatestUnreadMessageId(roomId, userId);
    }


    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean updateReadStatus(Long roomId, Long userId, LocalDateTime lastReadTime){        ChatMessageRead readStatus = lambdaQuery().eq(ChatMessageRead::getRoomId, roomId)
                .eq(ChatMessageRead::getUserId,userId).one();
        if (readStatus != null) {
            readStatus.setLastReadTime(lastReadTime);
            return updateById(readStatus);
        } else {
            // 如果没有记录，则插入新的记录
            readStatus = new ChatMessageRead();
            readStatus.setRoomId(roomId);
            readStatus.setUserId(userId);
            readStatus.setLastReadTime(lastReadTime);
            return save(readStatus);
        }
    }
}