package com.ly.model.entity.chat;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.ly.model.entity.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("chat_room")
public class ChatRoom extends BaseEntity {

    @TableField("customer1_id")
    private Long customer1Id;

    @TableField("customer2_id")
    private Long customer2Id;
}