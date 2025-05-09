package com.ly.chat.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.ly.chat.mapper.ChatRoomUserMapper;
import com.ly.chat.service.ChatRoomUserService;
import com.ly.model.entity.chat.ChatRoomUser;
import org.springframework.stereotype.Service;

@Service
public class ChatRoomUserServiceImpl extends ServiceImpl<ChatRoomUserMapper, ChatRoomUser> implements ChatRoomUserService {

}