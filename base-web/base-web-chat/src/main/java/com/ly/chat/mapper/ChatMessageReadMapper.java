package com.ly.chat.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ly.model.entity.chat.ChatMessageRead;
import org.apache.ibatis.annotations.MapKey;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 用户对聊天室已读状态的 Mapper 接口
 */
public interface ChatMessageReadMapper extends BaseMapper<ChatMessageRead> {

    @MapKey("user_id")
    List<Map<String, Object>> getUnreadMessageCountByRoomId(@Param("roomId") Long roomId);

    Long getLatestUnreadMessageId(@Param("roomId") Long roomId, @Param("userId") Long userId);

}