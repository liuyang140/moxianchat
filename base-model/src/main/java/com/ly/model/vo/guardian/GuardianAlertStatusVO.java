package com.ly.model.vo.guardian;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "今日是否已告警返回值")
public class GuardianAlertStatusVO {

    @Schema(description = "是否已告警")
    private Boolean alerted;
}