package com.ly.model.vo.guardian;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "守护时间规则展示")
public class ScheduleRuleVO {

    @Schema(description = "星期几（1-7），为 null 或 0 表示每天")
    private Integer weekday;

    @Schema(description = "开始时间 格式为 HH:mm:ss（例如 22:00:00）")
    private String startTime;

    @Schema(description = "结束时间 格式为 HH:mm:ss（例如 06:00:00）")
    private String endTime;
}