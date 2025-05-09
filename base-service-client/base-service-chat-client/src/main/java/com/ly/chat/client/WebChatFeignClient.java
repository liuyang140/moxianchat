package com.ly.chat.client;

import com.ly.common.config.feign.FeignConfig;
import com.ly.common.result.Result;
import com.ly.model.dto.chat.ChatMessageDTO;
import com.ly.model.dto.chat.RoomCreateDTO;
import com.ly.model.dto.chat.UpdateMessageDTO;
import com.ly.model.vo.customer.MatchUserVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "web-chat",  configuration = FeignConfig.class)
public interface WebChatFeignClient {

    @PostMapping("/room/createRoom")
    Result<MatchUserVo> createRoom(@RequestBody RoomCreateDTO roomCreateDTO);

    @PostMapping("/chatMessage/saveMessages")
    Result saveMessages(@RequestBody UpdateMessageDTO dto);

    @PostMapping("/chatMessage/recallMessages")
    Result<List<ChatMessageDTO>> recallMessages(@RequestBody UpdateMessageDTO dto);

    @GetMapping("/chatMessage/getRoomUserIds")
    Result<List<Long>> getRoomUserIds(@RequestParam(value ="roomId") Long roomId);

    @GetMapping("/chatMessageRead/unreadCountOne")
    Result<Long> unreadCountOne(@RequestParam(value = "roomId") Long roomId,
                                       @RequestParam(value = "userId") Long userId);

}
