package com.ly.model.enums;


import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ChatEventTypeEnum {

    CHAT(0, "聊天"),
    BIND(1, "上线"),
    RECALL(2, "撤回"),
    LOGIN(2, "登录"),
            ;

    private Integer value;
    private String comment;

    public static ChatEventTypeEnum fromValue(int value) {
        for (ChatEventTypeEnum type : values()) {
            if (type.value == value) {
                return type;
            }
        }
        return null; // 或者抛异常
    }

}
