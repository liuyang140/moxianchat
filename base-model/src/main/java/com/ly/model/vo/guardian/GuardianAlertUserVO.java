package com.ly.model.vo.guardian;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "待告警用户信息")
public class GuardianAlertUserVO {

    @Schema(description = "用户ID")
    private Long userId;

    @Schema(description = "用户手机号")
    private String phone;

    @Schema(description = "联系人手机号")
    private String contactPhone;

    @Schema(description = "是否已通知")
    private Boolean notified;
}