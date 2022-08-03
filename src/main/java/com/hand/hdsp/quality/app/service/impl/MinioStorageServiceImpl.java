package com.hand.hdsp.quality.app.service.impl;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import com.hand.hdsp.quality.app.service.MinioStorageService;
import feign.Response;
import io.choerodon.core.exception.CommonException;
import lombok.extern.slf4j.Slf4j;
import org.hzero.boot.file.FileClient;
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

    private final FileClient fileClient;

    public MinioStorageServiceImpl(FileClient fileClient) {
        this.fileClient = fileClient;
    }

    @Override
    public String uploadFile(Long organizationId, String bucketName, String directory, String fileName, Integer docType, String storageCode, MultipartFile multipartFile) {
        return fileClient.uploadFile(
                organizationId,
                bucketName,
                null,
                fileName,
                multipartFile
        );
    }


    @Override
    public InputStream downloadByUrl(Long organizationId, String bucketName, String storageCode, String url) {
        try {
            Response response = fileClient.downloadFileResponse(organizationId, bucketName, storageCode, url);
            return response.body().asInputStream();
        } catch (IOException e) {
            throw new CommonException(e);
        }
    }

    @Override
    public void deleteFileByUrl(Long organizationId, String bucketName, String storageCode, List<String> urls) {
        fileClient.deleteFileByUrl(organizationId, bucketName, storageCode, urls);
    }
}
