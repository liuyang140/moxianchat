package com.ly.model.vo.chat;

import com.ly.model.entity.customer.CustomerInfo;
import com.ly.model.vo.customer.CustomerUserVo;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

@Schema(description = "接收者信息")
@Data
@Accessors(chain = true)
public class ReceiverInfoVo {

    @Schema(description = "用户Id")
    private Long receiverId;

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

    public static ReceiverInfoVo buildReceiverUserVo(CustomerInfo customerInfo){
        return new ReceiverInfoVo()
                .setReceiverId(customerInfo.getId())
                .setNickname(customerInfo.getNickname())
                .setGender(customerInfo.getGender())
                .setAvatarUrl(customerInfo.getAvatarUrl())
                .setPhone(customerInfo.getPhone());
    }
}
