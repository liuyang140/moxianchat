package com.ly.model.entity.chat;

import com.baomidou.mybatisplus.annotation.*;
import com.ly.model.entity.base.BaseEntity;
import lombok.Data;

@Data
@TableName("chat_message")
public class ChatMessage extends BaseEntity {

    /** 聊天室ID */
    @TableField("room_id")
    private Long roomId;

    /** 发送者用户ID */
    @TableField("sender_id")
    private Long senderId;

    /** 消息内容 */
    @TableField("content")
    private String content;

    /** 消息类型（0-text, 1-image, 2-file 等） */
    @TableField("message_type")
    private Integer messageType;

    /** 是否被撤回 */
    @TableField("recalled")
    private Integer recalled;

    /*客户端发送时间戳*/
    @TableField("timestamp")
    private Long timestamp;

    /*聊天类型 0私聊，1群聊*/
    @TableField("chat_type")
    private Integer chatType;

    /*接收人用户*/
    @TableField("receiver_id")
    private Long receiverId;


}