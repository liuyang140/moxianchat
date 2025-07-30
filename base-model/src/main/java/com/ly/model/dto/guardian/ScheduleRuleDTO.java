package com.ly.model.dto.guardian;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "守护时间配置")
public class ScheduleRuleDTO {

    @Schema(description = "星期几（1-7），为 null 或 0 表示每天")
    private Integer weekday;

    @Schema(description = "开始时间，格式 HH:mm:ss")
    private String startTime;

    @Schema(description = "结束时间，格式 HH:mm:ss")
    private String endTime;
}