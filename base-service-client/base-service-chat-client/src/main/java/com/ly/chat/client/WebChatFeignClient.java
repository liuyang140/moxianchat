package com.ly.chat.client;

import com.ly.common.config.feign.FeignConfig;
import com.ly.common.result.Result;
import com.ly.model.dto.chat.ChatMessageDTO;
import com.ly.model.dto.chat.RoomCreateDTO;
import com.ly.model.dto.chat.UpdateMessageDTO;
import com.ly.model.vo.customer.CustomerUserVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "web-chat",  configuration = FeignConfig.class)
public interface WebChatFeignClient {

    @PostMapping("/room/createRoom")
    Result<CustomerUserVo> createRoom(@RequestBody RoomCreateDTO roomCreateDTO);

    @GetMapping("/room/getRoomUserIds")
    Result<List<Long>> getRoomUserIds(@RequestParam(value ="roomId") Long roomId);

    @PostMapping("/chatMessage/saveMessages")
    Result saveMessages(@RequestBody UpdateMessageDTO dto);

    @PostMapping("/chatMessage/recallMessages")
    Result<List<ChatMessageDTO>> recallMessages(@RequestBody UpdateMessageDTO dto);

    @GetMapping("/chatMessageRead/unreadCountOne")
    Result<Long> unreadCountOne(@RequestParam(value = "roomId") Long roomId,
                                       @RequestParam(value = "userId") Long userId);

    @GetMapping("/chatMessageRead/totalUnread")
    Result<Long> getTotalUnreadCount(@RequestParam Long userId);

}
