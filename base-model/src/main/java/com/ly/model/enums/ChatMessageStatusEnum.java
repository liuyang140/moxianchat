package com.ly.model.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ChatMessageStatusEnum {

    UNREAD(0, "未读"),
    READ(1, "已读"),
    ;

    private Integer value;
    private String comment;
}
