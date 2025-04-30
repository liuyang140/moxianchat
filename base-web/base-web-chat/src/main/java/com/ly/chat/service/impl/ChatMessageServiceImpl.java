package com.ly.chat.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.ly.chat.mapper.ChatMessageMapper;
import com.ly.chat.mapper.ChatRoomMapper;
import com.ly.chat.netty.websocket.ChannelManager;
import com.ly.model.dto.chat.ChatMessageDTO;
import com.ly.model.entity.chat.ChatMessage;
import com.ly.model.enums.ChatTypeEnum;
import io.netty.channel.Channel;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
public class ChatMessageServiceImpl {

    @Autowired
    private ChatMessageMapper chatMessageMapper;

    @Autowired
    private ChatRoomMapper chatRoomMapper;

    public void saveAndForward(JSONObject json) {
        ChatMessageDTO dto = json.toJavaObject(ChatMessageDTO.class);

        // 持久化消息
        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setRoomId(dto.getRoomId());
        chatMessage.setSenderId(dto.getSenderId());
        chatMessage.setContent(dto.getContent());
        chatMessage.setMessageType(dto.getMessageType());
        chatMessage.setTimestamp(dto.getTimestamp());
        chatMessageMapper.insert(chatMessage);

        // 转发消息（私聊）
        if (Objects.equals(dto.getChatType(), ChatTypeEnum.PRIVATE.getValue())) {
            Channel receiverChannel = ChannelManager.get(dto.getReceiverId());
            if (receiverChannel != null) {
                receiverChannel.writeAndFlush(new TextWebSocketFrame(JSON.toJSONString(dto)));
            }
        }

        // 群聊广播（如果需要）
        if (Objects.equals(dto.getChatType(), ChatTypeEnum.GROUP.getValue())) {
            List<Long> members = chatRoomMapper.getRoomMemberIds(dto.getRoomId());
            for (Long memberId : members) {
                if (memberId.equals(dto.getSenderId())) continue; // 不发给自己

                Channel ch = ChannelManager.get(memberId);
                if (ch != null) {
                    ch.writeAndFlush(new TextWebSocketFrame(JSON.toJSONString(dto)));
                }
            }
        }
    }
}