package com.ly.chat.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ly.model.entity.chat.ChatRoom;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ChatRoomMapper extends BaseMapper<ChatRoom> {

    List<Long> getRoomMemberIds(@Param("roomId") Long roomId);

    List<Long> getRoomIdsByUser(@Param("userId") Long userId);

    /*批量根据聊天室类型及id获取聊天室成员*/
    List<Long> getRoomsMemberIdsByType(@Param("roomIds") List<Long> roomIds, @Param("type") Integer type);
}