package com.ly.customer.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ly.customer.mapper.ChatRoomMapper;
import com.ly.customer.mapper.ChatRoomUserMapper;
import com.ly.customer.service.ChatRoomService;
import com.ly.customer.service.ChatRoomUserService;
import com.ly.model.entity.chat.ChatRoom;
import com.ly.model.entity.chat.ChatRoomUser;
import org.springframework.stereotype.Service;

@Service
public class ChatRoomUserServiceImpl extends ServiceImpl<ChatRoomUserMapper, ChatRoomUser> implements ChatRoomUserService {
}