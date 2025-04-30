package com.ly.chat.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ly.model.entity.chat.ChatRoomUser;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ChatRoomUserMapper extends BaseMapper<ChatRoomUser> {
    List<Long> selectRoomsByUsersAndType(@Param("userIds") List<Long> userIds,
                                         @Param("roomType") Integer roomType);
}