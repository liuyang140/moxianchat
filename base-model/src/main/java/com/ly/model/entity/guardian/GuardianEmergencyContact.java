package com.ly.model.entity.guardian;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.ly.model.entity.base.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "独居青年守护紧急联系人表")
@TableName("guardian_emergency_contact")
public class GuardianEmergencyContact extends BaseEntity {

    @Schema(description = "用户ID")
    @TableField("user_id")
    private Long userId;

    @Schema(description = "联系人姓名")
    @TableField("contact_name")
    private String contactName;

    @Schema(description = "联系人手机号")
    @TableField("contact_phone")
    private String contactPhone;
}