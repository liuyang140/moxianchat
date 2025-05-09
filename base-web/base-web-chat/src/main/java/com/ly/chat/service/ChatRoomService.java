package com.ly.chat.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ly.model.entity.chat.ChatRoom;
import com.ly.model.vo.chat.UserRoomUnreadMessagesVO;
import com.ly.model.vo.customer.CustomerUserVo;

import java.util.List;

public interface ChatRoomService extends IService<ChatRoom> {

    CustomerUserVo createRoom(Long userId, CustomerUserVo target);

    List<Long> getRoomUserIds(Long roomId);

    /**
     * 获取用户所有房间的未读消息数和最新消息
     */
    UserRoomUnreadMessagesVO getUnreadMessagesByUser();

}