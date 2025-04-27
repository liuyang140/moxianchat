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
    private String content;

    /** 消息类型（text, image, file 等） */
    @TableField("message_type")
    private String messageType;

    /** 是否被撤回 */
    @TableField("is_recalled")
    private Boolean isRecalled;


}