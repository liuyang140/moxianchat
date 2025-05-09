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
    JOIN_PRIVATE_ROOM(6, "加入私聊房间"),
    LEAVE_PRIVATE_ROOM(7, "离开私聊房间"),
    ONLINE_USERS(8, "推送当前房间在线用户列表"),
    KICK(9, "用户重连"),
    UNREAD_PUSH(10,"未读消息推送" ),
    UNREAD_COUNT_PUSH(11,"未读消息总数统计推送" )
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
