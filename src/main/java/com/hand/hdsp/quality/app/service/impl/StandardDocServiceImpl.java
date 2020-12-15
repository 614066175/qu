package com.hand.hdsp.quality.app.service.impl;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import javax.servlet.http.HttpServletResponse;

import com.hand.hdsp.quality.api.dto.StandardDocDTO;
import com.hand.hdsp.quality.app.service.MinioStorageService;
import com.hand.hdsp.quality.app.service.StandardDocService;
import com.hand.hdsp.quality.domain.entity.StandardDoc;
import com.hand.hdsp.quality.domain.repository.StandardDocRepository;
import com.hand.hdsp.quality.infra.constant.StandardDocConstant;
import com.hand.hdsp.quality.infra.mapper.StandardDocMapper;
import io.choerodon.core.domain.Page;
import io.choerodon.core.exception.CommonException;
import io.choerodon.mybatis.pagehelper.PageHelper;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import org.apache.commons.collections.CollectionUtils;
import org.hzero.export.vo.ExportParam;
import org.hzero.mybatis.domian.Condition;
import org.hzero.mybatis.util.Sqls;
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

    public StandardDocServiceImpl(StandardDocMapper standardDocMapper,
                                  StandardDocRepository standardDocRepository,
                                  MinioStorageService minioStorageService) {
        this.standardDocMapper = standardDocMapper;
        this.standardDocRepository = standardDocRepository;
        this.minioStorageService = minioStorageService;
    }

    @Override
    public Page<StandardDocDTO> list(PageRequest pageRequest, StandardDocDTO standardDocDTO) {
        return PageHelper.doPageAndSort(pageRequest, () -> standardDocMapper.list(standardDocDTO));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public StandardDocDTO create(StandardDocDTO standardDocDTO, MultipartFile multipartFile) {
        if (Objects.nonNull(multipartFile)) {
            handleStandardDocUpload(standardDocDTO, multipartFile);
        }
        standardDocRepository.insertDTOSelective(standardDocDTO);
        return standardDocDTO;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public StandardDocDTO update(StandardDocDTO standardDocDTO, MultipartFile multipartFile) {
        if (Objects.nonNull(multipartFile)) {
            handleStandardDocUpload(standardDocDTO, multipartFile);
        }
        standardDocRepository.updateByDTOPrimaryKeySelective(standardDocDTO);
        return standardDocRepository.selectDTOByPrimaryKeyAndTenant(standardDocDTO);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void remove(List<StandardDocDTO> standardDocDTOList, Long tenantId) {
        if (CollectionUtils.isEmpty(standardDocDTOList)) {
            return;
        }
        standardDocRepository.batchDTODelete(standardDocDTOList);
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
