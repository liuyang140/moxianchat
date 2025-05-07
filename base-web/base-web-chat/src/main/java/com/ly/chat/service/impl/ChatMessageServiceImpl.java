package com.ly.chat.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ly.chat.mapper.ChatMessageMapper;
import com.ly.chat.mapper.ChatRoomMapper;
import com.ly.chat.service.ChatMessageService;
import com.ly.model.dto.chat.ChatMessageDTO;
import com.ly.model.dto.chat.UpdateMessageDTO;
import com.ly.model.entity.chat.ChatMessage;
import com.ly.model.enums.ChatEventTypeEnum;
import com.ly.model.enums.ChatMessageStatusEnum;
import com.ly.model.enums.ChatRecallStatusEnum;
import com.ly.model.vo.base.PageVo;
import com.ly.model.vo.chat.ChatMessageVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Slf4j
@Service
public class ChatMessageServiceImpl extends ServiceImpl<ChatMessageMapper, ChatMessage> implements ChatMessageService {

    @Autowired
    private ChatMessageMapper chatMessageMapper;

    @Autowired
    private ChatRoomMapper chatRoomMapper;

    @Transactional(rollbackFor = Exception.class )
    @Override
    public void saveMessages(UpdateMessageDTO uDto) {
//        ChatMessageDTO dto = json.toJavaObject(ChatMessageDTO.class);
        List<ChatMessage> chatMessageList = CollUtil.newArrayList();
        uDto.getChatMessageDTOList().forEach(dto -> {
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
            chatMessageList.add(chatMessage);
//            chatMessageMapper.insert(chatMessage);
        });
        this.saveBatch(chatMessageList);

//        forwardMessage(dto);
    }

    /*private void forwardMessage(ChatMessageDTO dto) {
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
    }*/

    @Transactional(rollbackFor = Exception.class )
    @Override
    public List<ChatMessageDTO> recallMessages(UpdateMessageDTO uDto) {
//        ChatMessageDTO chatMessageDTO = json.toJavaObject(ChatMessageDTO.class);

        List<ChatMessage> chatMessageList = CollUtil.newArrayList();
        List<ChatMessageDTO> chatMessageDTOList = CollUtil.toList();
        uDto.getChatMessageDTOList().forEach(chatMessageDTO -> {
            ChatMessage message = this.getById(chatMessageDTO.getMessageId());
            if (message == null || !Objects.equals(message.getSenderId(), chatMessageDTO.getSenderId())) {
                log.info("非法撤回尝试：{}", chatMessageDTO);
                return;
            }

            // 更新数据库
            message.setRecalled(ChatRecallStatusEnum.RECALL.getValue());
            chatMessageList.add(message);

            // 构造撤回通知消息
            ChatMessageDTO recallNotice = new ChatMessageDTO();
            recallNotice.setType(ChatEventTypeEnum.RECALL.getValue());
            recallNotice.setChatType(chatMessageDTO.getChatType());
            recallNotice.setMessageId(chatMessageDTO.getMessageId());
            recallNotice.setRoomId(chatMessageDTO.getRoomId());
            recallNotice.setSenderId(chatMessageDTO.getSenderId());
            recallNotice.setReceiverId(chatMessageDTO.getReceiverId());
            recallNotice.setContent("消息已被撤回");
            chatMessageDTOList.add(recallNotice);
        });
        this.updateBatchById(chatMessageList);
        return chatMessageDTOList;

        // 广播给房间内所有其他成员
//        List<Long> members = chatRoomMapper.getRoomMemberIds(chatMessageDTO.getRoomId());
//        sendMesseageAll(members, chatMessageDTO, recallNotice);
    }

/*    private void sendMesseageAll(List<Long> members, ChatMessageDTO chatMessageDTO, ChatMessageDTO recallNotice) {
        for (Long memberId : members) {
            if (memberId.equals(chatMessageDTO.getSenderId())) continue;

            Channel ch = ChannelManager.get(memberId);
            if (ch != null) {
                ch.writeAndFlush(new TextWebSocketFrame(JSON.toJSONString(WsResult.ok(ChatEventTypeEnum.RECALL.getValue(), recallNotice))));
            }
        }
    }*/

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

    @Override
    public List<Long> getRoomUserIds(Long roomId) {
        return chatRoomMapper.getRoomMemberIds(roomId);
    }

}