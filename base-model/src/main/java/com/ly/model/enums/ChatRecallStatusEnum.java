package com.ly.model.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ChatRecallStatusEnum {

    UN_RECALL(0, "未被撤回"),
    RECALL(1, "已撤回"),
            ;

    private Integer value;
    private String comment;
}
