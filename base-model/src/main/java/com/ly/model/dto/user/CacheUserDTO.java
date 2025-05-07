package com.ly.model.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "缓存用户信息")
public class CacheUserDTO {
    @Schema(description = "缓存用户id")
    private Long id;

    @Schema(description = "客户昵称")
    private String nickname;

    @Schema(description = "性别")
    private Integer gender;

    @Schema(description = "头像")
    private String avatarUrl;

    @Schema(description = "电话")
    private String phone;

    @Schema(description = "1有效，2禁用")
    private Integer status;
}
