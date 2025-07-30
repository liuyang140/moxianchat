package com.ly.model.entity.guardian;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.ly.model.entity.base.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;


@Data
@Schema(description = "守护时间配置明细表（支持每日及每周按天配置）")
@TableName("guardian_schedule_rule")
public class GuardianScheduleRule extends BaseEntity {

    @Schema(description = "关联 guardian_config.id")
    @TableField("config_id")
    private Long configId;

    @Schema(description = "星期几，1=周一，...，7=周日，NULL或0表示每日生效")
    @TableField("weekday")
    private Integer weekday;

    @Schema(description = "守护开始时间")
    @TableField("start_time")
    private String startTime;

    @Schema(description = "守护结束时间")
    @TableField("end_time")
    private String endTime;
}