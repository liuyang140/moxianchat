package com.ly.model.vo.customer;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Schema(description = "匹配用户vo")
@Accessors(chain = true)
public class MatchUserVo {

    @Schema(description = "匹配用户Id")
    private Long customerId;

    @Schema(description = "用户昵称")
    private String nickname;

    @Schema(description = "性别")
    private Integer gender;

    @Schema(description = "头像")
    private String avatarUrl;

    @Schema(description = "电话")
    private String phone;

    @Schema(description = "距离，单位km")
    private double distanceKm;

/*    @Schema(description = "目标用户id")
    private Long targetCustomerId;*/

    @Schema(description = "房间id")
    private Long roomId;

}
