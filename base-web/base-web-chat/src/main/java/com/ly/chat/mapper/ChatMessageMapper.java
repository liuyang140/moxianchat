package com.ly.chat.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ly.model.entity.chat.ChatMessage;
import com.ly.model.vo.chat.ChatMessageVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 聊天消息Mapper
 */
public interface ChatMessageMapper extends BaseMapper<ChatMessage> {
    List<ChatMessageVo> getHistoryMessages(@Param("roomId") Long roomId,
                                           @Param("offset") int offset,
                                           @Param("size") int size);

    Long getHistoryMessagesCount(@Param("roomId") Long roomId,
                                           @Param("offset") int offset,
                                           @Param("size") int size);
}