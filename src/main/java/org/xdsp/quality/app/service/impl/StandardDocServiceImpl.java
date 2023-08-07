package org.xdsp.quality.app.service.impl;

import com.hand.hdsp.core.CommonGroupClient;
import com.hand.hdsp.core.domain.entity.CommonGroup;
import com.hand.hdsp.core.domain.repository.CommonGroupRepository;
import com.hand.hdsp.core.util.DiscoveryHelper;
import com.hand.hdsp.core.util.PageParseUtil;
import com.hand.hdsp.quality.api.dto.StandardDocDTO;
import com.hand.hdsp.quality.app.service.MinioStorageService;
import com.hand.hdsp.quality.app.service.StandardDocService;
import com.hand.hdsp.quality.domain.entity.DataStandard;
import com.hand.hdsp.quality.domain.entity.StandardDoc;
import com.hand.hdsp.quality.domain.repository.StandardDocRepository;
import com.hand.hdsp.quality.domain.repository.StandardGroupRepository;
import com.hand.hdsp.quality.infra.constant.ErrorCode;
import com.hand.hdsp.quality.infra.constant.StandardDocConstant;
import com.hand.hdsp.quality.infra.export.DocStandardExporter;
import com.hand.hdsp.quality.infra.export.ExportUtils;
import com.hand.hdsp.quality.infra.export.dto.DocStandardExportDTO;
import com.hand.hdsp.quality.infra.mapper.StandardDocMapper;
import io.choerodon.core.convertor.ApplicationContextHelper;
import io.choerodon.core.domain.Page;
import io.choerodon.core.exception.CommonException;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.hzero.boot.driver.infra.util.PageUtil;
import org.hzero.boot.file.FileClient;
import org.hzero.boot.platform.lov.annotation.ProcessLovValue;
import org.hzero.export.vo.ExportParam;
import org.hzero.mybatis.domian.Condition;
import org.hzero.mybatis.util.Sqls;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.xdsp.core.CommonGroupClient;
import org.xdsp.core.domain.entity.CommonGroup;
import org.xdsp.core.domain.repository.CommonGroupRepository;
import org.xdsp.core.util.DiscoveryHelper;
import org.xdsp.core.util.PageParseUtil;
import org.xdsp.quality.api.dto.StandardDocDTO;
import org.xdsp.quality.app.service.MinioStorageService;
import org.xdsp.quality.app.service.StandardDocService;
import org.xdsp.quality.domain.entity.DataStandard;
import org.xdsp.quality.domain.entity.StandardDoc;
import org.xdsp.quality.domain.repository.StandardDocRepository;
import org.xdsp.quality.domain.repository.StandardGroupRepository;
import org.xdsp.quality.infra.constant.ErrorCode;
import org.xdsp.quality.infra.constant.StandardDocConstant;
import org.xdsp.quality.infra.export.DocStandardExporter;
import org.xdsp.quality.infra.export.ExportUtils;
import org.xdsp.quality.infra.export.dto.DocStandardExportDTO;
import org.xdsp.quality.infra.mapper.StandardDocMapper;

import javax.servlet.http.HttpServletResponse;
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
    private final StandardGroupRepository standardGroupRepository;
    private final FileClient fileClient;
    @Value("${XDSP_PREVIEW_FILE_SERVICE_URL:}")
    private String url;
    @Value("${XDSP_PREVIEW_FILE_SERVICE:xdsp-file-preview}")
    private String serverName;

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    public StandardDocServiceImpl(StandardDocMapper standardDocMapper,
                                  StandardDocRepository standardDocRepository,
                                  MinioStorageService minioStorageService,
                                  DiscoveryHelper discoveryHelper,
                                  StandardGroupRepository standardGroupRepository,
                                  FileClient fileClient) {
        this.standardDocMapper = standardDocMapper;
        this.standardDocRepository = standardDocRepository;
        this.minioStorageService = minioStorageService;
        this.discoveryHelper = discoveryHelper;
        this.standardGroupRepository = standardGroupRepository;
        this.fileClient = fileClient;
    }

    @Override
    public List<StandardDocDTO> findLists(StandardDocDTO standardDocDTO) {
        List<StandardDocDTO> list = standardDocMapper.list(standardDocDTO);
        ExportUtils.decryptDocStandard(list);
        return list;
    }

    @Override
    public Page<StandardDocDTO> list(PageRequest pageRequest, StandardDocDTO standardDocDTO) {
        //分组查询时同时查询当前分组和当前分组子分组的数据标准
        Long groupId = standardDocDTO.getGroupId();
        if (ObjectUtils.isNotEmpty(groupId)) {
//            List<StandardGroupDTO> standardGroupDTOList = new ArrayList<>();
//            //查询子分组
//            findChildGroups(groupId,standardGroupDTOList);
//            //添加当前分组
//            standardGroupDTOList.add(StandardGroupDTO.builder().groupId(groupId).build());
//            Long[] groupIds = standardGroupDTOList.stream().map(StandardGroupDTO::getGroupId).toArray(Long[]::new);
//            standardDocDTO.setGroupArrays(groupIds);
            CommonGroupRepository commonGroupRepository = ApplicationContextHelper.getContext().getBean(CommonGroupRepository.class);
            CommonGroup commonGroup = commonGroupRepository.selectByPrimaryKey(groupId);
            CommonGroupClient commonGroupClient = ApplicationContextHelper.getContext().getBean(CommonGroupClient.class);
            List<CommonGroup> subGroup = commonGroupClient.getSubGroup(commonGroup);
            subGroup.add(commonGroup);
            standardDocDTO.setGroupArrays(subGroup.stream().map(CommonGroup::getGroupId).toArray(Long[]::new));
        }
        List<StandardDocDTO> list = standardDocMapper.list(standardDocDTO);
        ExportUtils.decryptDocStandard(list);
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
        }
        standardDocRepository.updateDTOAllColumnWhereTenant(standardDocDTO, standardDocDTO.getTenantId());
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
            if (StringUtils.isNotEmpty(s.getDocPath())) {
                fileClient.deleteFileByUrl(
                        tenantId,
                        StandardDocConstant.STANDARD_DOC_MINIO_BUCKET,
                        null,
                        Collections.singletonList(s.getDocPath())
                );
            }
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
    @ProcessLovValue(targetField = "standardDocDTOList")
    public List<DocStandardExportDTO> export(StandardDocDTO dto, ExportParam exportParam) {
        return ApplicationContextHelper.getContext().getBean(DocStandardExporter.class).export(dto);
    }


    /**
     * 获取预览接口url
     *
     * @return url
     */
    @Override
    public String previewUrl() {
        try {
            if (StringUtils.isNotBlank(url)) {
                return url;
            } else {
                return discoveryHelper.getAppUrlByName(serverName);
            }
        } catch (Exception e) {
            throw new CommonException("xsta.err.file_preview_not_exist");
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
