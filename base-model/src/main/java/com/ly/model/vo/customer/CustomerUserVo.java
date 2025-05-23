package com.ly.model.vo.customer;

import com.ly.model.dto.user.CacheUserDTO;
import com.ly.model.entity.customer.CustomerInfo;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Schema(description = "匹配用户vo")
@Accessors(chain = true)
public class CustomerUserVo {

    @Schema(description = "用户Id")
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

    public static CustomerUserVo buildCommonUserVo(CustomerInfo customerInfo){
        return new CustomerUserVo()
                .setCustomerId(customerInfo.getId())
                .setNickname(customerInfo.getNickname())
                .setGender(customerInfo.getGender())
                .setAvatarUrl(customerInfo.getAvatarUrl())
                .setPhone(customerInfo.getPhone());
    }

    public static CustomerUserVo buildUserDtoToVo(CacheUserDTO userDTO){
        return new CustomerUserVo()
                .setCustomerId(userDTO.getId())
                .setNickname(userDTO.getNickname())
                .setGender(userDTO.getGender())
                .setAvatarUrl(userDTO.getAvatarUrl())
                .setPhone(userDTO.getPhone());
    }


}
