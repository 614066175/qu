package org.xdsp.quality.app.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.List;

/**
 * <p>
 * 基于hzero-file进行储存插件
 * </p>
 *
 * @author isaac 2020/7/30 11:18
 * @since 1.0.0
 */
public interface MinioStorageService {

    /**
     * 上传文件到远程服务器
     *
     * @param organizationId tenantId
     * @param bucketName     bucketName
     * @param directory      directory
     * @param fileName       fileName
     * @param docType        docType
     * @param storageCode    storageCode
     * @param multipartFile  multipartFile
     * @return 该文件上传到远程服务器后的储存地址
     */
    String uploadFile(Long organizationId,
                      String bucketName,
                      String directory,
                      String fileName,
                      Integer docType,
                      String storageCode,
                      MultipartFile multipartFile);


    /**
     * 下载文件
     *
     * @param organizationId tenantId
     * @param bucketName     bucketName
     * @param storageCode    storageCode
     * @param url            url
     * @return InputStream
     */
    InputStream downloadByUrl(Long organizationId,
                              String bucketName,
                              String storageCode,
                              String url);

    /**
     * 根据url删除文件
     *
     * @param organizationId tenantId
     * @param bucketName     bucketName
     * @param storageCode    storageCode
     * @param urls           List<String>
     */
    void deleteFileByUrl(Long organizationId,
                         String bucketName,
                         String storageCode,
                         List<String> urls);
}