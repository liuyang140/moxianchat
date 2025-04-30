package com.ly.model.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ChatMessageTypeEnum {

    TEXT(0, "文本"),
    IMAGE(1, "图片"),
    FILE(2, "文件"),
    ;

    @EnumValue
    private Integer value;
    private String comment;
}
