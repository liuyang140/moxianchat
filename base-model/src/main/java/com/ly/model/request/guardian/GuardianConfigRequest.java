package com.ly.model.request.guardian;

import com.ly.model.dto.guardian.EmergencyContactDTO;
import com.ly.model.dto.guardian.ScheduleRuleDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "守护配置请求体")
public class GuardianConfigRequest {

    @Schema(description = "是否开启守护功能")
    private Boolean enabled;

    @Schema(description = "住址")
    private String address;

    @Schema(description = "紧急联系人列表")
    private List<EmergencyContactDTO> contacts;

    @Schema(description = "守护时间规则列表")
    private List<ScheduleRuleDTO> scheduleRules;
}