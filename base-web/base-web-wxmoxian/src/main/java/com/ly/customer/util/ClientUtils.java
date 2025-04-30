package com.ly.customer.util;

import com.ly.chat.client.ChatRoomFeignClient;
import com.ly.common.execption.LyException;
import com.ly.common.result.Result;
import com.ly.common.result.ResultCodeEnum;
import com.ly.model.dto.chat.RoomCreateDTO;
import com.ly.model.vo.customer.MatchUserVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ClientUtils {

    @Autowired
    private ChatRoomFeignClient roomFeignClient;

    public MatchUserVo createRoom(Long customerId,MatchUserVo matchUserVo){
        RoomCreateDTO roomCreateDTO = new RoomCreateDTO();
        roomCreateDTO.setCustomerId(customerId);
        roomCreateDTO.setTargetCustomer(matchUserVo);

        Result<MatchUserVo> result = roomFeignClient.createRoom(roomCreateDTO);
        if (result != null && result.isOk()) {
            return result.getData();
        } else {
            throw new LyException(ResultCodeEnum.FAIL.getCode(),"房间创建失败: " + (result != null ? result.getMessage() : "未知错误"));
        }
    }
}
