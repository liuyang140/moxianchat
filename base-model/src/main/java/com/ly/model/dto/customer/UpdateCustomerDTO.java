package com.ly.model.dto.customer;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(description = "更新用户信息")
public class UpdateCustomerDTO {

    @Schema(description = "用户id")
    @NotBlank(message = "用户id不能为空")
    private String id;

    @Schema(description = "客户昵称")
    private String nickname;

    @Schema(description = "头像")
    private String avatarUrl;

    @Schema(description = "电话")
    private String phone;

    @Schema(description = "性别 0-女 1-男")
    private Integer gender;
}
