package com.ly.model.entity.guardian;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.ly.model.entity.base.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Date;

@Data
@Schema(description = "独居青年守护功能配置表")
@TableName("guardian_config")
public class GuardianConfig extends BaseEntity {

    @Schema(description = "用户ID（唯一）")
    @TableField("user_id")
    private Long userId;

    @Schema(description = "是否开启守护功能")
    @TableField("enabled")
    private Boolean enabled;

    @Schema(description = "住址")
    @TableField("address")
    private String address;

    @Schema(description = "上次活跃时间（登录或点击“我已安全”）")
    @TableField("last_active_time")
    private LocalDateTime lastActiveTime;

    @Schema(description = "是否已触发告警（避免重复短信）")
    @TableField("notified")
    private Boolean notified;
}