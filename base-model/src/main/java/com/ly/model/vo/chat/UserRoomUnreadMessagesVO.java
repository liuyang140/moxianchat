package com.ly.model.vo.chat;

import lombok.Data;
import java.util.List;

@Data
public class UserRoomUnreadMessagesVO {

    private Long userId;  // 用户ID
    private List<RoomVO> rooms;  // 用户所有房间的列表

}