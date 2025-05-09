package com.ly.chat.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ly.chat.mapper.ChatMessageMapper;
import com.ly.chat.mapper.ChatMessageReadMapper;
import com.ly.chat.mapper.ChatRoomMapper;
import com.ly.chat.service.ChatMessageReadService;
import com.ly.model.dto.chat.ChatMessageDTO;
import com.ly.model.dto.chat.UnreadCountDTO;
import com.ly.model.entity.chat.ChatMessage;
import com.ly.model.entity.chat.ChatMessageRead;
import com.ly.model.entity.chat.ChatRoom;
import com.ly.model.enums.ChatEventTypeEnum;
import com.ly.model.vo.chat.RoomVO;
import com.ly.model.vo.chat.UserRoomUnreadMessagesVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 用户对聊天室已读状态的 Service 实现类
 */
@Service
public class ChatMessageReadServiceImpl extends ServiceImpl<ChatMessageReadMapper, ChatMessageRead> implements ChatMessageReadService {

    @Autowired
    private ChatRoomMapper chatRoomMapper;

    @Autowired
    private ChatMessageMapper chatMessageMapper;

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

    @Override
    public Long countUnreadMessages(Long roomId, Long userId) {
        return this.getBaseMapper().countUnreadMessages(roomId, userId);
    }

    @Override
    public void markAsRead(Long roomId, Long userId) {

        ChatMessageReadMapper baseMapper = this.getBaseMapper();
        // 查询是否存在
        LambdaQueryWrapper<ChatMessageRead> query = Wrappers.lambdaQuery(ChatMessageRead.class)
                .eq(ChatMessageRead::getRoomId, roomId)
                .eq(ChatMessageRead::getUserId, userId);

        ChatMessageRead exist = baseMapper.selectOne(query);

        if (Objects.isNull(exist)) {
            // 不存在：插入
            ChatMessageRead insert = new ChatMessageRead();
            insert.setRoomId(roomId);
            insert.setUserId(userId);
            insert.setLastReadTime(LocalDateTime.now());
            baseMapper.insert(insert);
        } else {
            // 存在：更新
            exist.setLastReadTime(LocalDateTime.now());
            baseMapper.updateById(exist);
        }
    }

    @Override
    public Long getTotalUnreadCount(Long userId) {
        return this.getBaseMapper().countTotalUnreadMessages(userId);
    }

}