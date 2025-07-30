package com.ly.model.vo.guardian;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "守护配置展示数据")
public class GuardianConfigVO {

    @Schema(description = "是否开启守护功能")
    private Boolean enabled;

    @Schema(description = "住址")
    private String address;

    @Schema(description = "紧急联系人列表")
    private List<EmergencyContactVO> contacts;

    @Schema(description = "守护时间配置列表")
    private List<ScheduleRuleVO> scheduleRules;
}