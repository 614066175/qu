package org.xdsp.quality.infra.export;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.hzero.mybatis.domian.Condition;
import org.hzero.mybatis.util.Sqls;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;
import org.xdsp.core.domain.entity.CommonGroup;
import org.xdsp.core.domain.repository.CommonGroupRepository;
import org.xdsp.quality.api.dto.StandardDocDTO;
import org.xdsp.quality.infra.export.dto.DocStandardExportDTO;
import org.xdsp.quality.infra.mapper.StandardDocMapper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.hzero.core.util.StringPool.COMMA;
import static org.xdsp.core.infra.constant.CommonGroupConstants.GroupType.DOC_STANDARD;

/**
 * @Title: FieldStandardExporter
 * @Description:
 * @author: lgl
 * @date: 2023/2/8 14:15
 */
@Component
public class DocStandardExporter implements Exporter<StandardDocDTO, List<DocStandardExportDTO>> {
    private final CommonGroupRepository commonGroupRepository;
    private final StandardDocMapper standardDocMapper;

    public DocStandardExporter(CommonGroupRepository commonGroupRepository, StandardDocMapper standardDocMapper) {
        this.commonGroupRepository = commonGroupRepository;
        this.standardDocMapper = standardDocMapper;
    }

    @Override
    public List<DocStandardExportDTO> export(StandardDocDTO dto) {
        //按条件导出标准，获取分组id，并导出分组数据
        if (StringUtils.isNotEmpty(dto.getExportIds())) {
            //导出指定标准
            List<Long> docIds = Arrays.stream(dto.getExportIds().split(COMMA)).map(Long::parseLong).collect(Collectors.toList());
            dto.setDocIds(docIds);
            //获取数据标准
            List<StandardDocDTO> docStandards = standardDocMapper.list(dto);
            List<Long> groupIds = docStandards.stream().map(StandardDocDTO::getGroupId).collect(Collectors.toList());
            //获取分组数据
            List<CommonGroup> commonGroups = commonGroupRepository.selectByCondition(Condition.builder(CommonGroup.class)
                    .andWhere(Sqls.custom()
                            .andIn(CommonGroup.FIELD_GROUP_ID, groupIds))
                    .build());
            return exportDocStandard(commonGroups, docStandards);
        } else if (dto.getGroupId() != null) {
            //导出指定分组
            List<CommonGroup> commonGroups = commonGroupRepository.selectByCondition(Condition.builder(CommonGroup.class)
                    .andWhere(Sqls.custom()
                            .andEqualTo(CommonGroup.FIELD_GROUP_ID, dto.getGroupId()))
                    .build());
            if (CollectionUtils.isEmpty(commonGroups)) {
                //分组未找到，导出所有
                return exportAll(dto);
            } else {
                dto.setGroupArrays(new Long[]{dto.getGroupId()});
                List<StandardDocDTO> docStandards = standardDocMapper.list(dto);
                return exportDocStandard(commonGroups, docStandards);
            }
        } else {
            //导出全部分组和符合条件数据标准数据
            return exportAll(dto);
        }
    }

    private List<DocStandardExportDTO> exportAll(StandardDocDTO dto) {
        //全部分组条件导出
        //添加查询所有父分组 并排序导出保证导入准确性
        List<CommonGroup> commonGroups = commonGroupRepository.selectByCondition(Condition.builder(CommonGroup.class)
                .andWhere(Sqls.custom()
                        .andEqualTo(CommonGroup.FIELD_TENANT_ID, dto.getTenantId())
                        .andEqualTo(CommonGroup.FIELD_PROJECT_ID, dto.getProjectId())
                        .andEqualTo(CommonGroup.FIELD_GROUP_TYPE, DOC_STANDARD))
                .build());
        if (CollectionUtils.isEmpty(commonGroups)) {
            return new ArrayList<>();
        }
        Long[] groupIds = commonGroups.stream().map(CommonGroup::getGroupId).toArray(Long[]::new);
        dto.setGroupArrays(groupIds);
        //按条件查询标准数据
        List<StandardDocDTO> standardDocDTOList = standardDocMapper.list(dto);
        return exportDocStandard(commonGroups, standardDocDTOList);
    }

    public  List<DocStandardExportDTO> exportDocStandard(List<CommonGroup> commonGroups, List<StandardDocDTO> standardDocDTOList) {
        List<DocStandardExportDTO> docStandardExportDTOList = new ArrayList<>();
        commonGroups.forEach(commonGroup -> {
            DocStandardExportDTO docStandardExportDTO = new DocStandardExportDTO();
            BeanUtils.copyProperties(commonGroup, docStandardExportDTO);
            int i = docStandardExportDTO.getGroupPath().lastIndexOf("/");
            if (i > 0) {
                docStandardExportDTO.setParentGroupPath(docStandardExportDTO.getGroupPath().substring(0, i));
            }
            docStandardExportDTOList.add(docStandardExportDTO);
        });
        //解密
        ExportUtils.decryptDocStandard(standardDocDTOList);
        docStandardExportDTOList.get(0).setStandardDocDTOList(standardDocDTOList);
        return docStandardExportDTOList;
    }
}
