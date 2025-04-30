package com.ly.chat.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ly.model.entity.chat.ChatRoom;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ChatRoomMapper extends BaseMapper<ChatRoom> {

    List<Long> getRoomMemberIds(@Param("roomId") Long roomId);
}