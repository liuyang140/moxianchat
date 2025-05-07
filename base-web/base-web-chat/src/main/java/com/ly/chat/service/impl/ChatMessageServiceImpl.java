package com.ly.chat.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ly.chat.mapper.ChatMessageMapper;
import com.ly.chat.mapper.ChatRoomMapper;
import com.ly.chat.netty.websocket.ChannelManager;
import com.ly.chat.service.ChatMessageService;
import com.ly.common.result.WsResult;
import com.ly.model.dto.chat.ChatMessageDTO;
import com.ly.model.entity.chat.ChatMessage;
import com.ly.model.enums.ChatEventTypeEnum;
import com.ly.model.enums.ChatMessageStatusEnum;
import com.ly.model.enums.ChatTypeEnum;
import com.ly.model.vo.base.PageVo;
import com.ly.model.vo.chat.ChatMessageVo;
import io.netty.channel.Channel;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Slf4j
@Service
public class ChatMessageServiceImpl extends ServiceImpl<ChatMessageMapper, ChatMessage> implements ChatMessageService {

    @Autowired
    private ChatMessageMapper chatMessageMapper;

    @Autowired
    private ChatRoomMapper chatRoomMapper;

    @Transactional(rollbackFor = Exception.class )
    @Override
    public void saveAndForward(JSONObject json) {
        ChatMessageDTO dto = json.toJavaObject(ChatMessageDTO.class);

        // 持久化消息
        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setRoomId(dto.getRoomId());
        chatMessage.setSenderId(dto.getSenderId());
        chatMessage.setContent(dto.getContent());
        chatMessage.setMessageType(dto.getMessageType());
        chatMessage.setTimestamp(dto.getTimestamp());
        chatMessage.setRecalled(dto. getMessageStatus());
        chatMessage.setChatType(dto.getChatType());
        chatMessage.setReceiverId(dto.getReceiverId());
        chatMessageMapper.insert(chatMessage);

        // 转发消息（私聊）
        if (Objects.equals(dto.getChatType(), ChatTypeEnum.PRIVATE.getValue())) {
            Channel receiverChannel = ChannelManager.get(dto.getReceiverId());
            if (receiverChannel != null) {
                receiverChannel.writeAndFlush(new TextWebSocketFrame(JSON.toJSONString(dto)));
            }
        }

        // 群聊广播
        if (Objects.equals(dto.getChatType(), ChatTypeEnum.GROUP.getValue())) {
            List<Long> memberList = chatRoomMapper.getRoomMemberIds(dto.getRoomId());
            Set<Long> memberSet = new HashSet<>(memberList);
            for (Long memberId : memberSet) {
                if (memberId.equals(dto.getSenderId())) continue; // 不发给自己

                Channel ch = ChannelManager.get(memberId);
                if (ch != null) {
                    ch.writeAndFlush(new TextWebSocketFrame(JSON.toJSONString(WsResult.ok(ChatEventTypeEnum.CHAT.getValue(), dto))));
                }
            }
        }
    }

    @Transactional(rollbackFor = Exception.class )
    @Override
    public void recallMessage(JSONObject json) {
        ChatMessageDTO chatMessageDTO = json.toJavaObject(ChatMessageDTO.class);

        ChatMessage message = this.getById(chatMessageDTO.getMessageId());
        if (message == null || !Objects.equals(message.getSenderId(), chatMessageDTO.getSenderId())) {
            log.info("非法撤回尝试：{}", json);
            return;
        }

        // 更新数据库
        message.setRecalled(ChatMessageStatusEnum.READ.getValue());
        this.updateById(message);

        // 构造撤回通知消息
        ChatMessageDTO recallNotice = new ChatMessageDTO();
        recallNotice.setType(ChatEventTypeEnum.RECALL.getValue());
        recallNotice.setMessageId(chatMessageDTO.getMessageId());
        recallNotice.setRoomId(chatMessageDTO.getRoomId());
        recallNotice.setSenderId(chatMessageDTO.getSenderId());
        recallNotice.setContent("消息已被撤回");

        // 广播给房间内所有其他成员
        List<Long> members = chatRoomMapper.getRoomMemberIds(chatMessageDTO.getRoomId());
        for (Long memberId : members) {
            if (memberId.equals(chatMessageDTO.getSenderId())) continue;

            Channel ch = ChannelManager.get(memberId);
            if (ch != null) {
                ch.writeAndFlush(new TextWebSocketFrame(JSON.toJSONString(WsResult.ok(ChatEventTypeEnum.RECALL.getValue(), recallNotice))));
            }
        }
    }

    @Override
    public PageVo<ChatMessageVo> getHistoryMessage(Long roomId, Integer page, Integer size){
            int offset = (page - 1) * size;
            List<ChatMessageVo> messages = chatMessageMapper.getHistoryMessages(roomId, offset, size);
            PageVo<ChatMessageVo> result = new PageVo<ChatMessageVo>()
                    .setPage(Long.valueOf(page))
                    .setLimit(Long.valueOf(size))
                    .setTotal(chatMessageMapper.getHistoryMessagesCount(roomId, offset, size))
                    .setRecords(messages);
            return result;

    }

}