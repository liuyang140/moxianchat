package com.ly.model.request.guardian;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "标记已告警用户请求体")
public class MarkNotifiedRequest {

    @Schema(description = "用户ID列表")
    private List<Long> userIds;
}