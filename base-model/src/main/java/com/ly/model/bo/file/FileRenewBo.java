package com.ly.model.bo.file;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "续签文件实体类")
public class FileRenewBo {

    @Schema(description = "文件对象名列表")
    private List<String> objectNameList;
}
