package com.ly.chat.controller;

import com.ly.chat.service.ChatRoomService;
import com.ly.common.result.Result;
import com.ly.model.dto.chat.RoomCreateDTO;
import com.ly.model.vo.customer.MatchUserVo;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Tag(name = "聊天室API接口管理")
@RestController
@RequestMapping("/room")
public class ChatRoomController {

    @Autowired
    private ChatRoomService chatRoomService;

    @PostMapping("/createRoom")
    public Result<MatchUserVo> createRoom(@RequestBody RoomCreateDTO roomCreateDTO) {
        return Result.ok(chatRoomService.createRoom(roomCreateDTO.getCustomerId(), roomCreateDTO.getTargetCustomer()));
    }
}
