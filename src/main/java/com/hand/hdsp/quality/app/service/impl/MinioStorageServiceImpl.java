package com.hand.hdsp.quality.app.service.impl;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import com.hand.hdsp.quality.app.service.MinioStorageService;
import feign.Response;
import io.choerodon.core.exception.CommonException;
import lombok.extern.slf4j.Slf4j;
import org.hzero.boot.file.feign.FileRemoteService;
import org.hzero.core.util.ResponseUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

/**
 * <p>
 * description
 * </p>
 *
 * @author isaac 2020/7/30 11:20
 * @since 1.0.0
 */
@Service
@Slf4j
public class MinioStorageServiceImpl implements MinioStorageService {

    private final FileRemoteService fileRemoteService;

    public MinioStorageServiceImpl(FileRemoteService fileRemoteService) {
        this.fileRemoteService = fileRemoteService;
    }

    @Override
    public String uploadFile(Long organizationId, String bucketName, String directory, String fileName, Integer docType, String storageCode, MultipartFile multipartFile) {
        ResponseEntity<String> result = fileRemoteService.uploadFile(
                organizationId,
                bucketName,
                null,
                fileName,
                null,
                storageCode,
                multipartFile
        );
        return ResponseUtils.getResponse(result, String.class,
                (httpStatus, response) -> {
                    log.error("标准文档上传失败，请检查hzero-file日志或者文件存储配置");
                    throw new CommonException(response);
                }, exceptionResponse -> {
                    log.error("标准文档上传失败，请检查hzero-file日志或者文件存储配置");
                    throw new CommonException(exceptionResponse.getMessage());
                });
    }


    @Override
    public InputStream downloadByUrl(Long organizationId, String bucketName, String storageCode, String url) {
        try {
            Response response = fileRemoteService.downloadByUrl(organizationId, bucketName, storageCode, url);
            return response.body().asInputStream();
        } catch (IOException e) {
            throw new CommonException(e);
        }
    }

    @Override
    public void deleteFileByUrl(Long organizationId, String bucketName, String storageCode, List<String> urls) {
        ResponseEntity<String> responseEntity = fileRemoteService.deleteFileByUrl(organizationId, bucketName, storageCode, urls);
        log.info("delete files: {}, result: {}", urls, responseEntity);
    }
}
