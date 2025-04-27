package com.ly.model.entity.chat;

import com.baomidou.mybatisplus.annotation.*;
import com.ly.model.entity.base.BaseEntity;
import lombok.Data;

@Data
@TableName("chat_room")
public class ChatRoom extends BaseEntity {

    /** 聊天室类型：private 或 group */
    private Integer type;

    /** 聊天室名称（仅群聊使用） */
    private String name;

}