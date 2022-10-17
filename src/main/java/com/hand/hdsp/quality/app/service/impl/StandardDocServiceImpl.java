package com.hand.hdsp.quality.app.service.impl;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletResponse;

import com.hand.hdsp.core.util.DiscoveryHelper;
import com.hand.hdsp.core.util.PageParseUtil;
import com.hand.hdsp.core.util.ProjectHelper;
import com.hand.hdsp.quality.api.dto.*;
import com.hand.hdsp.quality.app.service.MinioStorageService;
import com.hand.hdsp.quality.app.service.StandardDocService;
import com.hand.hdsp.quality.domain.entity.DataStandard;
import com.hand.hdsp.quality.domain.entity.StandardDoc;
import com.hand.hdsp.quality.domain.entity.StandardGroup;
import com.hand.hdsp.quality.domain.repository.StandardDocRepository;
import com.hand.hdsp.quality.domain.repository.StandardGroupRepository;
import com.hand.hdsp.quality.infra.constant.ErrorCode;
import com.hand.hdsp.quality.infra.constant.StandardDocConstant;
import com.hand.hdsp.quality.infra.mapper.DataStandardMapper;
import com.hand.hdsp.quality.infra.mapper.StandardDocMapper;
import io.choerodon.core.domain.Page;
import io.choerodon.core.exception.CommonException;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.hzero.boot.driver.infra.util.PageUtil;
import org.hzero.boot.platform.lov.annotation.ProcessLovValue;
import org.hzero.export.vo.ExportParam;
import org.hzero.mybatis.domian.Condition;
import org.hzero.mybatis.helper.DataSecurityHelper;
import org.hzero.mybatis.util.Sqls;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import static com.hand.hdsp.quality.infra.constant.StandardConstant.StandardType.DATA;
import static com.hand.hdsp.quality.infra.constant.StandardConstant.StandardType.DOC;

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
    @Value("${HDSP_PREVIEW_FILE_SERVICE:hdsp-file-preview}")
    private String url;

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    public StandardDocServiceImpl(StandardDocMapper standardDocMapper,
                                  StandardDocRepository standardDocRepository,
                                  MinioStorageService minioStorageService,
                                  DiscoveryHelper discoveryHelper,
                                  StandardGroupRepository standardGroupRepository) {
        this.standardDocMapper = standardDocMapper;
        this.standardDocRepository = standardDocRepository;
        this.minioStorageService = minioStorageService;
        this.discoveryHelper = discoveryHelper;
        this.standardGroupRepository = standardGroupRepository;
    }

    @Override
    public List<StandardDocDTO> findLists(StandardDocDTO standardDocDTO) {
        List<StandardDocDTO> list = standardDocMapper.list(standardDocDTO);
        for (StandardDocDTO docDTO : list) {
            decodeForStandardDocDTO(docDTO);
        }
        return list;
    }

    @Override
    public Page<StandardDocDTO> list(PageRequest pageRequest, StandardDocDTO standardDocDTO) {
        //分组查询时同时查询当前分组和当前分组子分组的数据标准
        Long groupId = standardDocDTO.getGroupId();
        if(ObjectUtils.isNotEmpty(groupId)){
            List<StandardGroupDTO> standardGroupDTOList = new ArrayList<>();
            //查询子分组
            findChildGroups(groupId,standardGroupDTOList);
            //添加当前分组
            standardGroupDTOList.add(StandardGroupDTO.builder().groupId(groupId).build());
            Long[] groupIds = standardGroupDTOList.stream().map(StandardGroupDTO::getGroupId).toArray(Long[]::new);
            standardDocDTO.setGroupArrays(groupIds);
        }
        List<StandardDocDTO> list = standardDocMapper.list(standardDocDTO);
        for (StandardDocDTO docDTO : list) {
            decodeForStandardDocDTO(docDTO);
        }
        return PageParseUtil.springPage2C7nPage(PageUtil.doPage(list, org.springframework.data.domain.PageRequest.of(pageRequest.getPage(), pageRequest.getSize())));
    }

    private void findChildGroups(Long groupId, List<StandardGroupDTO> standardGroups) {
        List<StandardGroupDTO> standardGroupDTOList = standardGroupRepository.selectDTOByCondition(Condition.builder(StandardGroup.class).andWhere(Sqls.custom()
                        .andEqualTo(StandardGroup.FIELD_PARENT_GROUP_ID,groupId))
                .build());
        if(CollectionUtils.isNotEmpty(standardGroupDTOList)){
            standardGroups.addAll(standardGroupDTOList);
            standardGroupDTOList.forEach(standardGroupDTO -> findChildGroups(standardGroupDTO.getGroupId(),standardGroups));
        }
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
        } else {
            //没有上传文件，清除文件
            standardDocDTO.setDocPath(null);
            standardDocDTO.setDocName(null);
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
    @ProcessLovValue(targetField = "standardDocDTOList")
    public List<StandardDocGroupDTO> export(StandardDocDTO dto, ExportParam exportParam) {
        List<StandardDocGroupDTO> standardDocGroupDTOList = new ArrayList<>();
        StandardDocGroupDTO standardDocGroupDTO = new StandardDocGroupDTO();
        Long projectId = ProjectHelper.getProjectId();
        int level = 1;
        if (ObjectUtils.isNotEmpty(dto.getGroupId())) {
            //分组条件导出
            StandardGroupDTO groupDTO = standardGroupRepository.selectDTOByCondition(Condition.builder(StandardGroup.class).andWhere(Sqls.custom()
                    .andEqualTo(StandardGroup.FIELD_TENANT_ID, dto.getTenantId())
                    .andEqualTo(StandardGroup.FIELD_PROJECT_ID, projectId)
                    .andEqualTo(StandardGroup.FIELD_GROUP_ID, dto.getGroupId(), true)
            ).build()).get(0);
            //获取设置当前分组的父分组编码
            if (ObjectUtils.isNotEmpty(groupDTO.getParentGroupId())) {
                StandardGroupDTO parentGroupDTO = standardGroupRepository.selectDTOByPrimaryKey(groupDTO.getParentGroupId());
                groupDTO.setParentGroupCode(parentGroupDTO.getGroupCode());
            }
            BeanUtils.copyProperties(groupDTO, standardDocGroupDTO);
            List<StandardGroupDTO> standardGroups = new ArrayList<>();
            //导出分组下条件筛选后的数据标准
            Long groupId = standardDocGroupDTO.getGroupId();
            if (ObjectUtils.isNotEmpty(groupId)) {
                //添加当前分组
                standardGroups.add(StandardGroupDTO.builder().groupId(groupId).build());
                Long[] groupIds = standardGroups.stream().map(StandardGroupDTO::getGroupId).toArray(Long[]::new);
                dto.setGroupArrays(groupIds);
            }
            //当前目录和子目录的数据标准的集合，与查询保持一致
            List<StandardDocDTO> standardDocs = standardDocMapper.list(dto);
            standardDocGroupDTO.setStandardDocDTOList(standardDocs);
            standardDocGroupDTO.setGroupLevel(level);
            standardDocGroupDTOList.add(standardDocGroupDTO);
            //添加查询父分组 并排序导出保证导入准确性
            List<StandardDocGroupDTO> standardDocGroupDTOS = new ArrayList<>();
            if (ObjectUtils.isNotEmpty(groupDTO.getParentGroupId())) {
                findParentGroups(groupDTO.getParentGroupId(), standardDocGroupDTOS, level);
            }
            standardDocGroupDTOList.addAll(standardDocGroupDTOS);
            return standardDocGroupDTOList.stream().sorted(Comparator.comparing(StandardDocGroupDTO::getGroupLevel).reversed()).collect(Collectors.toList());
        } else {
            //全部分组条件导出
            //添加查询所有父分组 并排序导出保证导入准确性
            List<StandardGroupDTO> standardGroupDTOList = standardGroupRepository.selectDTOByCondition(Condition.builder(StandardGroup.class).andWhere(Sqls.custom()
                            .andEqualTo(StandardGroup.FIELD_TENANT_ID, dto.getTenantId())
                            .andEqualTo(StandardGroup.FIELD_PROJECT_ID, dto.getProjectId())
                            .andEqualTo(StandardGroup.FIELD_STANDARD_TYPE, DOC))
                    .build());
            standardGroupDTOList.forEach(standardGroupDTO -> {
                if (ObjectUtils.isEmpty(standardGroupDTO.getParentGroupId())) {
                    //从所有的根目录 向下查询
                    StandardDocGroupDTO standardDocGroupDto = new StandardDocGroupDTO();
                    BeanUtils.copyProperties(standardGroupDTO, standardDocGroupDto);
                    //根目录数据标准列表
                    List<StandardDocDTO> dataStandardDTOList = standardDocMapper.list(StandardDocDTO.builder().groupArrays(new Long[]{standardDocGroupDto.getGroupId()}).build());
                    if (DataSecurityHelper.isTenantOpen() && org.apache.commons.collections4.CollectionUtils.isNotEmpty(dataStandardDTOList)) {
                        dataStandardDTOList.forEach(dataStandardDTO -> {
                            if (StringUtils.isNotEmpty(dataStandardDTO.getChargeName())) {
                                dataStandardDTO.setChargeName(DataSecurityHelper.decrypt(dataStandardDTO.getChargeName()));
                            }
                            if (StringUtils.isNotEmpty(dataStandardDTO.getChargeTel())) {
                                dataStandardDTO.setChargeTel(DataSecurityHelper.decrypt(dataStandardDTO.getChargeTel()));
                            }
                            if (StringUtils.isNotEmpty(dataStandardDTO.getChargeEmail())) {
                                dataStandardDTO.setChargeEmail(DataSecurityHelper.decrypt(dataStandardDTO.getChargeEmail()));
                            }
                            if (StringUtils.isNotEmpty(dataStandardDTO.getChargeDeptName())) {
                                dataStandardDTO.setChargeDeptName(DataSecurityHelper.decrypt(dataStandardDTO.getChargeDeptName()));
                            }
                        });
                    }
                    standardDocGroupDto.setStandardDocDTOList(dataStandardDTOList);
                    standardDocGroupDto.setGroupLevel(level);
                    standardDocGroupDTOList.add(standardDocGroupDto);
                    findSortedChildGroups(standardDocGroupDto, level, standardDocGroupDTOList);
                }
            });
            return standardDocGroupDTOList.stream().sorted(Comparator.comparing(StandardDocGroupDTO::getGroupLevel)).collect(Collectors.toList());
        }
    }

    private void findSortedChildGroups(StandardDocGroupDTO parentStandardDocGroupDTO, int level, List<StandardDocGroupDTO> standardDocGroupDTOList) {
        level++;
        List<StandardGroupDTO> standardGroupDTOList = standardGroupRepository.selectDTOByCondition(Condition.builder(StandardGroup.class).andWhere(Sqls.custom()
                        .andEqualTo(StandardGroup.FIELD_PARENT_GROUP_ID, parentStandardDocGroupDTO.getGroupId()))
                .build());
        if (org.apache.commons.collections4.CollectionUtils.isNotEmpty(standardGroupDTOList)) {
            int finalLevel = level;
            standardGroupDTOList.forEach(standardGroupDTO -> {
                StandardDocGroupDTO standardDocGroupDTO = new StandardDocGroupDTO();
                BeanUtils.copyProperties(standardGroupDTO, standardDocGroupDTO);
                standardDocGroupDTO.setGroupLevel(finalLevel);
                //子目录数据标准列表
                List<StandardDocDTO> standardDocDTOList = standardDocMapper.list(StandardDocDTO.builder().groupArrays(new Long[]{standardDocGroupDTO.getGroupId()}).build());
                if (DataSecurityHelper.isTenantOpen() && org.apache.commons.collections4.CollectionUtils.isNotEmpty(standardDocDTOList)) {
                    standardDocDTOList.forEach(standardDocDTO -> {
                        if (StringUtils.isNotEmpty(standardDocDTO.getChargeName())) {
                            standardDocDTO.setChargeName(DataSecurityHelper.decrypt(standardDocDTO.getChargeName()));
                        }
                        if (StringUtils.isNotEmpty(standardDocDTO.getChargeTel())) {
                            standardDocDTO.setChargeTel(DataSecurityHelper.decrypt(standardDocDTO.getChargeTel()));
                        }
                        if (StringUtils.isNotEmpty(standardDocDTO.getChargeEmail())) {
                            standardDocDTO.setChargeEmail(DataSecurityHelper.decrypt(standardDocDTO.getChargeEmail()));
                        }
                        if (StringUtils.isNotEmpty(standardDocDTO.getChargeDeptName())) {
                            standardDocDTO.setChargeDeptName(DataSecurityHelper.decrypt(standardDocDTO.getChargeDeptName()));
                        }
                    });
                }
                standardDocGroupDTO.setStandardDocDTOList(standardDocDTOList);
                //设置父分组code
                standardDocGroupDTO.setParentGroupCode(parentStandardDocGroupDTO.getGroupCode());
                standardDocGroupDTOList.add(standardDocGroupDTO);
                findSortedChildGroups(standardDocGroupDTO, finalLevel, standardDocGroupDTOList);
            });
        }
    }

    private void findParentGroups(Long groupId, List<StandardDocGroupDTO> standardGroups, int level) {
        StandardDocGroupDTO dataStandardGroupDTO = new StandardDocGroupDTO();
        List<StandardGroupDTO> standardGroupDTOList = standardGroupRepository.selectDTOByCondition(Condition.builder(StandardGroup.class).andWhere(Sqls.custom()
                        .andEqualTo(StandardGroup.FIELD_GROUP_ID, groupId))
                .build());
        level++;
        if (org.apache.commons.collections4.CollectionUtils.isNotEmpty(standardGroupDTOList)) {
            int finalLevel = level;
            standardGroupDTOList.forEach(parentStandardGroupDTO -> {
                BeanUtils.copyProperties(parentStandardGroupDTO, dataStandardGroupDTO);
                //获取设置当前分组的父分组编码
                if (ObjectUtils.isNotEmpty(dataStandardGroupDTO.getGroupId())) {
                    StandardGroupDTO parentGroupDTO = standardGroupRepository.selectDTOByPrimaryKey(dataStandardGroupDTO.getGroupId());
                    dataStandardGroupDTO.setGroupLevel(finalLevel);
                    standardGroups.add(dataStandardGroupDTO);
                    if (ObjectUtils.isNotEmpty(parentGroupDTO.getParentGroupId())) {
                        StandardGroup group = standardGroupRepository.selectByPrimaryKey(parentGroupDTO.getParentGroupId());
                        dataStandardGroupDTO.setParentGroupCode(group.getGroupCode());
                        findParentGroups(parentGroupDTO.getParentGroupId(), standardGroups, finalLevel);
                    }
                }
            });
        }
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
