package com.ly.model.vo.file;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@Schema(name = "MinioFileDTO", description = "文件上传响应体")
public class MinioFileVo {
    @Schema(description = "存储对象名", example = "1/uuid_filename.png")
    private String objectName;

    @Schema(description = "可访问 URL", example = "http://host/bucket/filename")
    private String fileUrl;
}
