package com.ly.model.entity.chat;

import com.baomidou.mybatisplus.annotation.*;
import com.ly.model.entity.base.BaseEntity;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;


@Data
@TableName("chat_message_read")
@Accessors(chain = true)
public class ChatMessageRead extends BaseEntity {

    /**
     * 聊天室ID
     */
    @TableField("room_id")
    private Long roomId;

    /**
     * 用户ID
     */
    @TableField("user_id")
    private Long userId;

    /**
     * 用户最后阅读消息的时间
     */
    @TableField("last_read_time")
    private LocalDateTime lastReadTime;


}