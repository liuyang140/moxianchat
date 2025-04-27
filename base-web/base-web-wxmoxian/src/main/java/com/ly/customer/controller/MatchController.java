package com.ly.customer.controller;

import com.ly.common.result.Result;
import com.ly.common.result.ResultCodeEnum;
import com.ly.customer.service.MatchService;
import com.ly.model.dto.customer.UpdateLocationDTO;
import com.ly.model.vo.customer.CustomerInfoVo;
import com.ly.model.vo.customer.MatchUserVo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@Tag(name = "匹配API接口管理")
@RestController
@RequestMapping("/match")
public class MatchController {

    @Autowired
    private MatchService matchService;

    // 上传经纬度
    @PostMapping("/updateLocation")
    @Operation(summary = "上传经纬度")
    public Result updateLocation(@RequestBody UpdateLocationDTO dto) {
        matchService.updateLocation(dto.getCustomerId(), dto.getLatitude(), dto.getLongitude());
        return Result.ok();
    }

    // 匹配附近用户
    @GetMapping("/match/one")
    @Operation(summary = "匹配附近用户")
    public Result<MatchUserVo> matchAndCreateRoom(@Schema(description = "用户ID可不传，后端调试用") @RequestParam(value = "customerId",  required = false) Long customerId,
                                                            @Schema(description = "距离，默认10，单位km")@RequestParam(defaultValue = "10",value = "initKm",required = false) Double initKm,
                                                            @Schema(description = "最大距离，默认50，单位km") @RequestParam(defaultValue = "50",value = "maxKm",required = false) Double maxKm,
                                                            @Schema(description = "递增距离，默认5，单位km") @RequestParam(defaultValue = "5", value = "stepKm",required = false) Double stepKm
    ) {
        MatchUserVo match = matchService.matchUser(customerId,initKm,maxKm,stepKm);
        if (match == null) return Result.build(ResultCodeEnum.NO_MATCH);
        return Result.ok(match);
    }

}