package com.hand.hdsp.quality.app.service.impl;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import javax.servlet.http.HttpServletResponse;

import com.hand.hdsp.core.util.PageParseUtil;
import com.hand.hdsp.quality.api.dto.NameStandardDTO;
import com.hand.hdsp.quality.api.dto.StandardDocDTO;
import com.hand.hdsp.quality.app.service.MinioStorageService;
import com.hand.hdsp.quality.app.service.StandardDocService;
import com.hand.hdsp.quality.domain.entity.DataStandard;
import com.hand.hdsp.quality.domain.entity.NameStandard;
import com.hand.hdsp.quality.domain.entity.StandardDoc;
import com.hand.hdsp.quality.domain.repository.StandardDocRepository;
import com.hand.hdsp.quality.infra.constant.ErrorCode;
import com.hand.hdsp.quality.infra.constant.StandardDocConstant;
import com.hand.hdsp.quality.infra.mapper.StandardDocMapper;
import com.hand.hdsp.quality.infra.util.EurekaUtil;
import io.choerodon.core.domain.Page;
import io.choerodon.core.exception.CommonException;
import io.choerodon.mybatis.pagehelper.PageHelper;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.hzero.boot.driver.infra.util.PageUtil;
import org.hzero.export.vo.ExportParam;
import org.hzero.mybatis.domian.Condition;
import org.hzero.mybatis.helper.DataSecurityHelper;
import org.hzero.mybatis.util.Sqls;
import org.springframework.beans.factory.annotation.Value;
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
    private final EurekaUtil eurekaUtil;
    @Value("${HDSP_PREVIEW_FILE_SERVICE:hdsp-file-preview}")
    private String url;

    public StandardDocServiceImpl(StandardDocMapper standardDocMapper,
                                  StandardDocRepository standardDocRepository,
                                  MinioStorageService minioStorageService,
                                  EurekaUtil eurekaUtil) {
        this.standardDocMapper = standardDocMapper;
        this.standardDocRepository = standardDocRepository;
        this.minioStorageService = minioStorageService;
        this.eurekaUtil = eurekaUtil;
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
                        .andEqualTo(StandardDoc.FIELD_STANDARD_CODE, standardDocDTO.getStandardCode()))
                .build());
        //标准编码存在
        if (CollectionUtils.isNotEmpty(standardDocDTOList)) {
            throw new CommonException(ErrorCode.CODE_ALREADY_EXISTS);
        }
        // 标准名称存在
        standardDocDTOList = standardDocRepository.selectDTOByCondition(Condition.builder(StandardDoc.class)
                .andWhere(Sqls.custom()
                        .andEqualTo(StandardDoc.FIELD_STANDARD_NAME, standardDocDTO.getStandardName())
                        .andEqualTo(StandardDoc.FIELD_TENANT_ID, standardDocDTO.getTenantId()))
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
                        .andEqualTo(StandardDoc.FIELD_STANDARD_NAME, standardDocDTO.getStandardName()))
                .build());
        if (dtoList.size() > 1 || (dtoList.size() == 1 && !dtoList.get(0).getStandardCode().equals(standardDocDTO.getStandardCode()))) {
            throw new CommonException(ErrorCode.DOC_STANDARD_NAME_ALREADY_EXIST);
        }
        if (Objects.nonNull(multipartFile)) {
            handleStandardDocUpload(standardDocDTO, multipartFile);
        }
        Optional.ofNullable(standardDocDTO.getStandardDesc()).orElseGet(() -> {
            standardDocMapper.updateDesc(standardDocDTO);
            return null;
        });
        standardDocRepository.updateByDTOPrimaryKeySelective(standardDocDTO);
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
                        .andEqualTo(StandardDoc.FIELD_PROJECT_ID, standardDocDTO.getProjectId())
                        .andEqualTo(StandardDoc.FIELD_TENANT_ID, standardDocDTO.getTenantId())
                ).build());
        if (CollectionUtils.isNotEmpty(standardDocs)) {
            standardDocDTO = standardDocs.get(0);
        } else {
            throw new CommonException("hdsp.xqua.error.no-permission");
        }
        try {
            String docName = URLEncoder.encode(standardDocDTO.getDocName(), "UTF-8");
            response.setHeader("content-disposition", "attachment;filename=" + docName);
            InputStream in = minioStorageService.downloadByUrl(
                    standardDocDTO.getTenantId(),
                    StandardDocConstant.STANDARD_DOC_MINIO_BUCKET,
                    null,
                    standardDocDTO.getDocPath()
            );
            int count;
            byte[] by = new byte[1024];
            OutputStream out = response.getOutputStream();
            while ((count = in.read(by)) != -1) {
                //将缓冲区的数据输出到浏览器
                out.write(by, 0, count);
            }
            in.close();
            out.flush();
            out.close();
        } catch (Exception e) {
            throw new CommonException("hdsp.xqua.error.file.download.fail", e);
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
            return eurekaUtil.getAppByName(url).getHomePageUrl();
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
