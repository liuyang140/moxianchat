package com.ly.chat.controller;

import com.ly.chat.service.ChatRoomService;
import com.ly.common.result.Result;
import com.ly.model.dto.chat.RoomCreateDTO;
import com.ly.model.vo.chat.UserRoomUnreadMessagesVo;
import com.ly.model.vo.customer.CustomerUserVo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "聊天室API接口管理（chat-api）")
@RestController
@RequestMapping("/room")
public class ChatRoomController {

    @Autowired
    private ChatRoomService chatRoomService;

    @Operation(summary = "创建房间")
    @PostMapping("/createRoom")
    public Result<CustomerUserVo> createRoom(@RequestBody RoomCreateDTO roomCreateDTO) {
        return Result.ok(chatRoomService.createRoom(roomCreateDTO.getCustomerId(), roomCreateDTO.getTargetCustomer()));
    }

    @Operation(summary = "获取房间内所有用户id")
    @GetMapping("/getRoomUserIds")
    public Result<List<Long>> getRoomUserIds(@RequestParam(value ="roomId") Long roomId){
        return Result.ok(chatRoomService.getRoomUserIds(roomId));
    }

    @Operation(summary = "获取用户所有房间的未读消息数和最新消息")
    @GetMapping("/unreadByUserId")
    public Result<UserRoomUnreadMessagesVo> getUnreadMessagesByUserId() {
        return Result.ok(chatRoomService.getUnreadMessagesByUser());
    }
}
