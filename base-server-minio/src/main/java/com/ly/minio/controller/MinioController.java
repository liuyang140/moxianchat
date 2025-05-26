package com.ly.minio.controller;

import cn.hutool.core.io.IoUtil;
import com.ly.common.result.Result;
import com.ly.minio.service.MinioService;
import com.ly.model.bo.file.FileRenewBo;
import com.ly.model.vo.file.MinioFileVo;
import io.minio.GetPresignedObjectUrlArgs;
import io.minio.StatObjectArgs;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Mono;

import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

@RestController
@RequestMapping("/api/files")
@RequiredArgsConstructor
@Tag(name = "文件存储API接口（file-api)", description = "MinIO 文件上传/下载/删除接口")
public class MinioController {

    private final MinioService minioService;

    @Operation(summary = "上传文件")
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Mono<Result<MinioFileVo>> upload(@Parameter(description = "文件", required = true) @RequestPart("file") FilePart filePart) throws Exception {
        Mono<MinioFileVo> minioFileVoMono = minioService.uploadFile(filePart);
        return minioFileVoMono.map(Result::ok);
    }

    @Operation(summary = "下载文件（后端使用）")
    @GetMapping("/download")
    public void download(@Parameter(description = "文件对象名") @RequestParam(value="objectName") String objectName, HttpServletResponse response) throws Exception {
        try (InputStream stream = minioService.downloadFile(objectName)) {
            response.setHeader("Content-Disposition", "attachment; fileName=" + URLEncoder.encode(objectName, StandardCharsets.UTF_8));
            IoUtil.copy(stream, response.getOutputStream());
            response.flushBuffer();
        }
    }

    @Operation(summary = "删除文件（后端使用）")
    @DeleteMapping("/delete")
    public Result<Void> delete(@Parameter(description = "文件对象名") @RequestParam(value = "objectName") String objectName) throws Exception {
        minioService.deleteFile(objectName);
        return Result.ok();
    }

    @Operation(summary = "续签文件")
    @GetMapping("/renew-url")
    public Mono<Result<MinioFileVo>> renewUrl(@Parameter(description = "文件名") @RequestParam("存储对象名") String objectName) {
        return minioService.renewPresignedUrl(objectName)
                .map(Result::ok)
                .onErrorResume(e -> Mono.just(Result.fail()));
    }

    @Operation(summary = "批量续签文件")
    @PostMapping("/renew-url/batch")
    public Mono<Result<List<MinioFileVo>>> renewUrlBatch(@Parameter(description = "文件名列表") @RequestBody FileRenewBo bo) {
        return minioService.renewPresignedUrls(bo.getObjectNameList())
                .map(Result::ok)
                .onErrorResume(e -> Mono.just(Result.fail()));
    }

}
