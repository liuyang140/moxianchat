package com.ly.model.dto.chat;

import com.ly.model.vo.customer.MatchUserVo;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "房间创建信息")
public class RoomCreateDTO {

    @Schema(description = "用户id")
    private Long customerId;

    @Schema(description = "目标用户")
    private MatchUserVo targetCustomer;


}
