package com.ly.model.vo.chat;

import com.ly.model.dto.chat.ChatMessageDTO;
import com.ly.model.vo.customer.CustomerInfoVo;
import lombok.Data;

@Data
public class RoomVo {

    private Long roomId;  // 房间ID
    private Integer roomType; // 房间类型：私聊或群聊
    private String roomName;  // 房间名称
    private Long roomReceiverId; //房间消息接收人Id
    private Integer unreadCount;  // 未读消息数
    private ChatMessageDTO latestMessage;  // 最新消息
}