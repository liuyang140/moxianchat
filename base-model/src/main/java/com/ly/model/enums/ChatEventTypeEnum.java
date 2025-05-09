package com.ly.model.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ChatEventTypeEnum {

    CHAT(0, "聊天"),
    BIND(1, "上线"),
    RECALL(2, "撤回"),
    PING(3, "心跳包"),
    JOIN_GROUP_ROOM(4, "加入群聊房间"),
    LEAVE_GROUP_ROOM(5, "离开群聊房间"),
    ONLINE_USERS(6, "推送当前房间在线用户列表"),
    KICK(7, "用户重连"),
    UNREAD_NOTIFY(8,"未读消息更新" );

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
