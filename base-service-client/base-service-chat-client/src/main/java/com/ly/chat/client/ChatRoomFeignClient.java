package com.ly.chat.client;

import com.ly.common.config.feign.FeignConfig;
import com.ly.common.result.Result;
import com.ly.model.dto.chat.RoomCreateDTO;
import com.ly.model.vo.customer.MatchUserVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "web-chat",  configuration = FeignConfig.class)
public interface ChatRoomFeignClient {

    @PostMapping("/room/createRoom")
    public Result<MatchUserVo> createRoom(@RequestBody RoomCreateDTO roomCreateDTO);

}
