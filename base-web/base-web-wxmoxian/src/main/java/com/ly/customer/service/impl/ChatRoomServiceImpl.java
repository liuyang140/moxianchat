package com.ly.customer.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ly.customer.mapper.ChatRoomMapper;
import com.ly.customer.service.ChatRoomService;
import com.ly.model.entity.chat.ChatRoom;
import org.springframework.stereotype.Service;

@Service
public class ChatRoomServiceImpl extends ServiceImpl<ChatRoomMapper, ChatRoom> implements ChatRoomService {
}