package com.hand.hdsp.quality.app.service.impl;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import javax.servlet.http.HttpServletResponse;

import com.hand.hdsp.core.util.DiscoveryHelper;
import com.hand.hdsp.core.util.PageParseUtil;
import com.hand.hdsp.quality.api.dto.StandardDocDTO;
import com.hand.hdsp.quality.app.service.MinioStorageService;
import com.hand.hdsp.quality.app.service.StandardDocService;
import com.hand.hdsp.quality.domain.entity.DataStandard;
import com.hand.hdsp.quality.domain.entity.StandardDoc;
import com.hand.hdsp.quality.domain.repository.StandardDocRepository;
import com.hand.hdsp.quality.infra.constant.ErrorCode;
import com.hand.hdsp.quality.infra.constant.StandardDocConstant;
import com.hand.hdsp.quality.infra.mapper.StandardDocMapper;
import io.choerodon.core.domain.Page;
import io.choerodon.core.exception.CommonException;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.hzero.boot.driver.infra.util.PageUtil;
import org.hzero.export.vo.ExportParam;
import org.hzero.mybatis.domian.Condition;
import org.hzero.mybatis.helper.DataSecurityHelper;
import org.hzero.mybatis.util.Sqls;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

/**
 * <p>标准文档管理表应用服务默认实现</p>
 *
 * @author hua.shi@hand-china.com 2020-11-24 17:50:14
 */
@Service
public class StandardDocServiceImpl implements StandardDocService {

    private final StandardDocMapper standardDocMapper;
    private final StandardDocRepository standardDocRepository;
    private final MinioStorageService minioStorageService;
    private final DiscoveryHelper discoveryHelper;
    @Value("${HDSP_PREVIEW_FILE_SERVICE:hdsp-file-preview}")
    private String url;

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    public StandardDocServiceImpl(StandardDocMapper standardDocMapper,
                                  StandardDocRepository standardDocRepository,
                                  MinioStorageService minioStorageService,
                                  DiscoveryHelper discoveryHelper) {
        this.standardDocMapper = standardDocMapper;
        this.standardDocRepository = standardDocRepository;
        this.minioStorageService = minioStorageService;
        this.discoveryHelper = discoveryHelper;
    }

    @Override
    public Page<StandardDocDTO> list(PageRequest pageRequest, StandardDocDTO standardDocDTO) {
        List<StandardDocDTO> list = standardDocMapper.list(standardDocDTO);
        for (StandardDocDTO docDTO : list) {
            decodeForStandardDocDTO(docDTO);
        }
        return PageParseUtil.springPage2C7nPage(PageUtil.doPage(list, org.springframework.data.domain.PageRequest.of(pageRequest.getPage(), pageRequest.getSize())));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public StandardDocDTO create(StandardDocDTO standardDocDTO, MultipartFile multipartFile) {
        List<StandardDocDTO> standardDocDTOList = standardDocRepository.selectDTOByCondition(Condition.builder(StandardDoc.class)
                .andWhere(Sqls.custom()
                        .andEqualTo(StandardDoc.FIELD_TENANT_ID, standardDocDTO.getTenantId())
                        .andEqualTo(StandardDoc.FIELD_STANDARD_CODE, standardDocDTO.getStandardCode())
                        .andEqualTo(StandardDoc.FIELD_PROJECT_ID, standardDocDTO.getProjectId()))
                .build());
        //标准编码存在
        if (CollectionUtils.isNotEmpty(standardDocDTOList)) {
            throw new CommonException(ErrorCode.CODE_ALREADY_EXISTS);
        }
        // 标准名称存在
        standardDocDTOList = standardDocRepository.selectDTOByCondition(Condition.builder(StandardDoc.class)
                .andWhere(Sqls.custom()
                        .andEqualTo(StandardDoc.FIELD_STANDARD_NAME, standardDocDTO.getStandardName())
                        .andEqualTo(StandardDoc.FIELD_TENANT_ID, standardDocDTO.getTenantId())
                        .andEqualTo(StandardDoc.FIELD_PROJECT_ID, standardDocDTO.getProjectId()))
                .build());
        //标准名称存在
        if (CollectionUtils.isNotEmpty(standardDocDTOList)) {
            throw new CommonException(ErrorCode.DOC_STANDARD_NAME_ALREADY_EXIST);
        }
        if (Objects.nonNull(multipartFile)) {
            handleStandardDocUpload(standardDocDTO, multipartFile);
        }
        standardDocRepository.insertDTOSelective(standardDocDTO);
        return standardDocDTO;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public StandardDocDTO update(StandardDocDTO standardDocDTO, MultipartFile multipartFile) {
        // 验证标准名称是否已存在
        List<StandardDocDTO> dtoList = standardDocRepository.selectDTOByCondition(Condition.builder(DataStandard.class)
                .andWhere(Sqls.custom()
                        .andEqualTo(StandardDoc.FIELD_TENANT_ID, standardDocDTO.getTenantId())
                        .andEqualTo(StandardDoc.FIELD_STANDARD_NAME, standardDocDTO.getStandardName())
                        .andEqualTo(StandardDoc.FIELD_PROJECT_ID, standardDocDTO.getProjectId()))
                .build());
        if (dtoList.size() > 1 || (dtoList.size() == 1 && !dtoList.get(0).getStandardCode().equals(standardDocDTO.getStandardCode()))) {
            throw new CommonException(ErrorCode.DOC_STANDARD_NAME_ALREADY_EXIST);
        }
        if (Objects.nonNull(multipartFile)) {
            handleStandardDocUpload(standardDocDTO, multipartFile);
        }else{
            //没有上传文件，清除文件
            standardDocDTO.setDocPath(null);
            standardDocDTO.setDocName(null);
        }
        standardDocRepository.updateDTOAllColumnWhereTenant(standardDocDTO,standardDocDTO.getTenantId());
        return standardDocRepository.selectDTOByPrimaryKeyAndTenant(standardDocDTO);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void remove(List<StandardDocDTO> standardDocDTOList, Long tenantId) {
        if (CollectionUtils.isEmpty(standardDocDTOList)) {
            return;
        }
        standardDocRepository.batchDTODeleteByPrimaryKey(standardDocDTOList);
        // 删minio
        for (StandardDocDTO s : standardDocDTOList) {
            minioStorageService.deleteFileByUrl(
                    tenantId,
                    StandardDocConstant.STANDARD_DOC_MINIO_BUCKET,
                    null,
                    Collections.singletonList(s.getDocPath())
            );
        }
    }

    @Override
    public void downloadStandardDoc(StandardDocDTO standardDocDTO, HttpServletResponse response) {
        List<StandardDocDTO> standardDocs = standardDocRepository.selectDTOByCondition(
                Condition.builder(StandardDoc.class).where(Sqls.custom()
                        .andEqualTo(StandardDoc.FIELD_DOC_ID, standardDocDTO.getDocId())
                        .andEqualTo(StandardDoc.FIELD_TENANT_ID, standardDocDTO.getTenantId())
                ).build());
        if (CollectionUtils.isNotEmpty(standardDocs)) {
            standardDocDTO = standardDocs.get(0);
        } else {
            throw new CommonException(ErrorCode.DOC_NO_FILE);
        }
        // 读取文件获取输入流
        response.setContentType("application/octet-stream;charset=utf-8");
        try (InputStream inputStream = getFileInputStream(standardDocDTO.getDocPath());
             OutputStream output = new BufferedOutputStream(response.getOutputStream());
             ByteArrayOutputStream bos = new ByteArrayOutputStream();) {
            assert inputStream != null;
            int count;
            byte[] bs = new byte[1024];
            while ((count = inputStream.read(bs)) != -1) {
                bos.write(bs, 0, count);
            }
            bs = bos.toByteArray();
            // 设置下载文件的名称
            String fileName = String.format("attachment;filename=%s", URLEncoder.encode(standardDocDTO.getDocName(), StandardCharsets.UTF_8.name()));
            // 作为附件下载
            response.setHeader(HttpHeaders.CONTENT_DISPOSITION, fileName);
            output.write(bs);
        } catch (Exception e) {
            throw new CommonException(ErrorCode.FILE_DOWNLOAD_FAIL, e);
        }
    }

    /**
     * 读取文件获取输入流
     *
     * @param path 路径名
     * @return 输入流
     */
    public static InputStream getFileInputStream(String path) {
        URL url;
        try {
            url = new URL(path);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            //设置超时间为3秒
            conn.setConnectTimeout(3 * 1000);
            //防止屏蔽程序抓取而返回403错误
            conn.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");
            //得到输入流
            return conn.getInputStream();
        } catch (Exception e) {
            throw new CommonException("读取网络文件异常:" + e);
        }
    }

    @Override
    public List<StandardDocDTO> export(StandardDocDTO dto, ExportParam exportParam, PageRequest pageRequest) {
        return list(pageRequest, dto);
    }

    private void decodeForStandardDocDTO(StandardDocDTO dto) {
        if (DataSecurityHelper.isTenantOpen()) {
            // 解密电话号码
            if (StringUtils.isNotEmpty(dto.getChargeTel())) {
                dto.setChargeTel(DataSecurityHelper.decrypt(dto.getChargeTel()));
            }
            // 解密邮箱地址
            if (StringUtils.isNotEmpty(dto.getChargeEmail())) {
                dto.setChargeEmail(DataSecurityHelper.decrypt(dto.getChargeEmail()));
            }
            // 解密部门名称
            if (StringUtils.isNotEmpty(dto.getChargeDeptName())) {
                dto.setChargeDeptName(DataSecurityHelper.decrypt(dto.getChargeDeptName()));
            }
            if (StringUtils.isNotEmpty(dto.getChargeName())) {
                dto.setChargeName(DataSecurityHelper.decrypt(dto.getChargeName()));
            }
        }

    }

    /**
     * 获取预览接口url
     *
     * @return url
     */
    @Override
    public String previewUrl() {
        try {
            return discoveryHelper.getAppUrlByName(url);
        } catch (Exception e) {
            throw new CommonException("hdsp.xsta.err.file_preview_not_exist");
        }
    }


    private void handleStandardDocUpload(StandardDocDTO standardDocDTO, MultipartFile multipartFile) {
        String oldDocPath = standardDocDTO.getDocPath();
        String originalFilename = multipartFile.getOriginalFilename();
        // 上传到remote hzero-file
        String standardDocPath = minioStorageService.uploadFile(
                standardDocDTO.getTenantId(),
                StandardDocConstant.STANDARD_DOC_MINIO_BUCKET,
                null,
                originalFilename,
                null,
                null,
                multipartFile
        );
        standardDocDTO.setDocName(originalFilename);
        standardDocDTO.setDocPath(standardDocPath);
        // 删除原有文件
        if (Objects.nonNull(oldDocPath) && !"".equals(oldDocPath)) {
            minioStorageService.deleteFileByUrl(
                    standardDocDTO.getTenantId(),
                    StandardDocConstant.STANDARD_DOC_MINIO_BUCKET,
                    null,
                    Collections.singletonList(oldDocPath)
            );
        }
    }

}
