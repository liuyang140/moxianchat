package com.ly.model.dto.chat;

import lombok.Data;

@Data
public class ChatMessageDTO {
    private String type;         // 事件类型：chat、bind、recall、login、ping等
    private Integer chatType;    // 聊天类型：0-私聊，1-群聊
    private Long roomId;         // 房间ID（私聊时用于区分会话，群聊表示群组）
    private Long senderId;       // 消息发送者ID
    private Long receiverId;     // 私聊接收方ID（群聊可忽略）
    private String content;      // 消息正文
    private Integer messageType;  // 消息类型：text、image、file、system等
    private Long timestamp;      // 客户端发送时间戳
}