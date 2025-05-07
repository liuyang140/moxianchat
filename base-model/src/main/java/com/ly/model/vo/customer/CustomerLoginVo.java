package com.ly.model.vo.customer;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class CustomerLoginVo {

    @Schema(description = "用户id")
    private Long customerId;

    @Schema(description = "客户昵称")
    private String nickname;

    @Schema(description = "性别")
    private Integer gender;

    @Schema(description = "头像")
    private String avatarUrl;

    @Schema(description = "手机号码")
    private String phone;
}