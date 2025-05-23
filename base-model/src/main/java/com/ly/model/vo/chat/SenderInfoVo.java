package com.ly.model.vo.chat;

import com.ly.model.entity.customer.CustomerInfo;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Schema(description = "发送者信息")
@Accessors(chain = true)
public class SenderInfoVo {

    @Schema(description = "用户Id")
    private Long senderId;

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

    public static SenderInfoVo buildSenderInfoVo(CustomerInfo customerInfo){
        return new SenderInfoVo()
                .setSenderId(customerInfo.getId())
                .setNickname(customerInfo.getNickname())
                .setGender(customerInfo.getGender())
                .setAvatarUrl(customerInfo.getAvatarUrl())
                .setPhone(customerInfo.getPhone());
    }

}
