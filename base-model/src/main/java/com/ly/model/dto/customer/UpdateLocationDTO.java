package com.ly.model.dto.customer;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "更新用户位置信息DTO")
@Data
public class UpdateLocationDTO {
    @Schema(description = "用户ID", required = true)
    private Long customerId;

    @Schema(description = "纬度", required = true)
    private Double latitude;

    @Schema(description = "经度", required = true)
    private Double longitude;
}
