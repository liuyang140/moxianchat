package com.ly.minio.service;

import com.ly.model.vo.file.MinioFileVo;
import org.springframework.http.codec.multipart.FilePart;
import reactor.core.publisher.Mono;

import java.io.InputStream;
import java.util.List;

public interface MinioService {
    Mono<MinioFileVo> uploadFile(FilePart file) throws Exception;
    InputStream downloadFile(String filename) throws Exception;
    void deleteFile(String filename) throws Exception;

    Mono<MinioFileVo> renewPresignedUrl(String objectName);

    Mono<List<MinioFileVo>> renewPresignedUrls(List<String> objectNameList);
}