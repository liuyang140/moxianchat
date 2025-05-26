package com.ly.minio.service.impl;

import com.ly.common.util.UserCacheUtils;
import com.ly.minio.property.MinioProperties;
import com.ly.minio.service.MinioService;
import com.ly.model.vo.file.MinioFileVo;
import io.minio.*;
import io.minio.http.Method;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MinioServiceImpl implements MinioService {
    private final MinioClient minioClient;
    private final MinioProperties properties;
    private final UserCacheUtils userCacheUtils;

    @PostConstruct
    public void ensureBucketExists() {
        try {
            boolean exists = minioClient.bucketExists(BucketExistsArgs.builder()
                    .bucket(properties.getBucketName()).build());
            if (!exists) {
                minioClient.makeBucket(MakeBucketArgs.builder()
                        .bucket(properties.getBucketName()).build());
            }
        } catch (Exception e) {
            throw new RuntimeException("MinIO bucket 初始化失败", e);
        }
    }

    @Override
    public Mono<MinioFileVo> uploadFile(FilePart filePart) {
        Long userId = userCacheUtils.getLoginUserId();
        String objectName = userId + "/" + UUID.randomUUID() + "_" + filePart.filename();

        return Mono.fromCallable(() -> {
            File tempFile = File.createTempFile("upload_", "_" + filePart.filename());
            tempFile.deleteOnExit();
            return tempFile;
        }).flatMap(tempFile ->
                filePart.transferTo(tempFile.toPath())
                        .then(Mono.fromCallable(() -> {
                            try (InputStream is = new FileInputStream(tempFile)) {
                                minioClient.putObject(PutObjectArgs.builder()
                                        .bucket(properties.getBucketName())
                                        .object(objectName)
                                        .stream(is, tempFile.length(), -1)
                                        .contentType(Files.probeContentType(tempFile.toPath()))
                                        .build());
                            }

                            // 使用 MinIO SDK 生成预签名 URL（有效期1小时）
                            String presignedUrl = minioClient.getPresignedObjectUrl(
                                    GetPresignedObjectUrlArgs.builder()
                                            .method(Method.GET)
                                            .bucket(properties.getBucketName())
                                            .object(objectName)
                                            .expiry(60 * 60 * 24) // 有效期：1小时
                                            .build()
                            );

                            return new MinioFileVo(objectName, presignedUrl);
                        }))
        );
    }

    @Override
    public InputStream downloadFile(String filename) throws Exception {
        return minioClient.getObject(GetObjectArgs.builder()
                .bucket(properties.getBucketName())
                .object(filename)
                .build());
    }

    @Override
    public void deleteFile(String filename) throws Exception {
        minioClient.removeObject(RemoveObjectArgs.builder()
                .bucket(properties.getBucketName())
                .object(filename)
                .build());
    }

    @Override
    public Mono<MinioFileVo> renewPresignedUrl(String objectName) {
        return Mono.fromCallable(() -> {
            // 校验对象是否存在
            minioClient.statObject(
                    StatObjectArgs.builder()
                            .bucket(properties.getBucketName())
                            .object(objectName)
                            .build()
            );

            // 生成新的预签名URL，有效期 1 小时
            String url = minioClient.getPresignedObjectUrl(
                    GetPresignedObjectUrlArgs.builder()
                            .method(Method.GET)
                            .bucket(properties.getBucketName())
                            .object(objectName)
                            .expiry(60 * 60 * 24)
                            .build()
            );

            return new MinioFileVo(objectName, url);
        });
    }

    @Override
    public Mono<List<MinioFileVo>> renewPresignedUrls(List<String> objectNames) {
        return Flux.fromIterable(objectNames)
                .flatMap(objectName -> Mono.fromCallable(() -> {
                    // 校验对象是否存在
                    minioClient.statObject(
                            StatObjectArgs.builder()
                                    .bucket(properties.getBucketName())
                                    .object(objectName)
                                    .build()
                    );

                    // 生成预签名URL
                    String url = minioClient.getPresignedObjectUrl(
                            GetPresignedObjectUrlArgs.builder()
                                    .method(Method.GET)
                                    .bucket(properties.getBucketName())
                                    .object(objectName)
                                    .expiry(60 * 60 * 24)
                                    .build()
                    );

                    return new MinioFileVo(objectName, url);
                }).onErrorResume(e -> {
                    // 如果单个失败，返回一个空对象或日志记录，也可跳过
                    return Mono.empty();
                }))
                .collectList();
    }

}