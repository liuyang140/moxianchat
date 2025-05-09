package com.ly.model.vo.chat;

import com.ly.model.dto.chat.ChatMessageDTO;
import lombok.Data;

@Data
public class RoomVO {

    private Long roomId;  // 房间ID
    private Integer type; // 私聊或群聊
    private String roomName;  // 房间名称
    private Integer unreadCount;  // 未读消息数
    private ChatMessageDTO latestMessage;  // 最新消息

}