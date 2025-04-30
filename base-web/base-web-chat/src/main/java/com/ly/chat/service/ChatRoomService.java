package com.ly.chat.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ly.model.entity.chat.ChatRoom;
import com.ly.model.vo.customer.MatchUserVo;

import java.util.List;

public interface ChatRoomService extends IService<ChatRoom> {

    MatchUserVo createRoom(Long userId, MatchUserVo target);

}