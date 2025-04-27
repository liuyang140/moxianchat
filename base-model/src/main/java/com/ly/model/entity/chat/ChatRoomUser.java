package com.ly.model.entity.chat;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.ly.model.entity.base.BaseEntity;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("chat_room_user")
public class ChatRoomUser extends BaseEntity {

    /** 聊天室ID */
    @TableField("room_id")
    private Long roomId;

    /** 用户ID */
    @TableField("user_id")
    private Long userId;

    /** 加入时间 */
    @TableField("join_time")
    private LocalDateTime joinTime;

    /** 是否为群主 */
    @TableField("is_owner")
    private Boolean isOwner;

}