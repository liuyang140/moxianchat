package com.ly.model.vo.chat;

import lombok.Data;
import java.util.List;

@Data
public class UserRoomUnreadMessagesVo {

    private Long customerId;  // 用户ID
    private List<RoomVo> rooms;  // 用户所有房间的列表

}