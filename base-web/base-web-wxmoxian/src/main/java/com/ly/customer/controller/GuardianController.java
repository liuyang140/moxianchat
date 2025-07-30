package com.ly.customer.controller;

import com.ly.common.result.Result;
import com.ly.customer.service.GuardianService;
import com.ly.model.request.guardian.GuardianConfigRequest;
import com.ly.model.request.guardian.MarkNotifiedRequest;
import com.ly.model.vo.guardian.GuardianConfigVO;
import com.ly.model.vo.guardian.GuardianAlertStatusVO;
import com.ly.model.vo.guardian.GuardianAlertUserVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@Tag(name = "独居青年守护API接口管理（customer-api）")
@RestController
@RequestMapping("/api/guardian")
public class GuardianController {

    @Autowired
    private GuardianService guardianService;

    @Operation(summary = "查询守护配置")
    @GetMapping("/config")
    public Result<GuardianConfigVO> getGuardianConfig() {
        return Result.ok(guardianService.getGuardianConfig());
    }

    @Operation(summary = "保存/更新守护配置")
    @PostMapping("/config")
    public Result<Void> saveOrUpdateConfig(@RequestBody GuardianConfigRequest req) {
        guardianService.saveOrUpdateConfig(req);
        return Result.ok();
    }

    @Operation(summary = "我已安全/活跃刷新")
    @PostMapping("/active")
    public Result<Void> refreshActive() {
        guardianService.refreshActive();
        return Result.ok();
    }

    @Operation(summary = "今日是否已告警")
    @GetMapping("/alert_status")
    public Result<GuardianAlertStatusVO> getAlertStatus() {
        return Result.ok(guardianService.getAlertStatus());
    }

    @Operation(summary = "定时任务接口 - 获取需告警用户")
    @GetMapping("/check_alert_users")
    public Result<List<GuardianAlertUserVO>> getUsersNeedAlert() {
        return Result.ok(guardianService.getUsersNeedAlert());
    }

    @Operation(summary = "标记已告警用户")
    @PostMapping("/mark-notified")
    public Result<Void> markUsersNotified(@RequestBody MarkNotifiedRequest req) {
        guardianService.markUsersNotified(req);
        return Result.ok();
    }
} 