package com.ly.model.entity.guardian;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.ly.model.entity.base.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Date;

@Data
@Schema(description = "独居青年守护通知日志表")
@TableName("guardian_notify_log")
public class GuardianNotifyLog extends BaseEntity {

    @Schema(description = "用户ID")
    @TableField("user_id")
    private Long userId;

    @Schema(description = "通知类型 WECHAT-0 SMS-1")
    @TableField("notify_type")
    private Integer notifyType;

    @Schema(description = "通知目标（仅短信）")
    @TableField("contact_phone")
    private String contactPhone;

    @Schema(description = "发送状态 0 失败，1成功")
    @TableField("status")
    private Integer status;

    @Schema(description = "失败原因")
    @TableField("reason")
    private String reason;

    @Schema(description = "通知时间")
    @TableField("notify_time")
    private LocalDateTime notifyTime;
}