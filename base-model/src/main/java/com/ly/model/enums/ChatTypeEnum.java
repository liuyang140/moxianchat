package com.ly.model.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ChatTypeEnum {

    PRIVATE(0, "私聊"),
    GROUP(1, "群聊"),
    ;

    @EnumValue
    private Integer value;
    private String comment;

}
