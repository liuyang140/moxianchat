package com.ly.model.dto.chat;

import lombok.Data;

@Data
public class UnreadCountDTO {
    private Long roomId;
    private Integer unreadCount;
}