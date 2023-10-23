package org.xdsp.quality.infra.export;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.hzero.mybatis.domian.Condition;
import org.hzero.mybatis.util.Sqls;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;
import org.xdsp.core.domain.entity.CommonGroup;
import org.xdsp.core.domain.repository.CommonGroupRepository;
import org.xdsp.quality.api.dto.DataFieldDTO;
import org.xdsp.quality.api.dto.ReferenceDataDTO;
import org.xdsp.quality.api.dto.StandardExtraDTO;
import org.xdsp.quality.domain.entity.StandardExtra;
import org.xdsp.quality.domain.repository.ReferenceDataRepository;
import org.xdsp.quality.domain.repository.StandardExtraRepository;
import org.xdsp.quality.infra.constant.PlanConstant;
import org.xdsp.quality.infra.export.dto.FieldStandardExportDTO;
import org.xdsp.quality.infra.mapper.DataFieldMapper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static org.hzero.core.util.StringPool.COMMA;
import static org.xdsp.core.infra.constant.CommonGroupConstants.GroupType.FIELD_STANDARD;
import static org.xdsp.quality.infra.constant.StandardConstant.StandardType.FIELD;

/**
 * @Title: FieldStandardExporter
 * @Description:
 * @author: lgl
 * @date: 2023/2/8 14:15
 */
@Component
public class FieldStandardExporter implements Exporter<DataFieldDTO, List<FieldStandardExportDTO>> {
    private final CommonGroupRepository commonGroupRepository;
    private final DataFieldMapper dataFieldMapper;
    private final ReferenceDataRepository referenceDataRepository;
    private final StandardExtraRepository standardExtraRepository;

    public FieldStandardExporter(CommonGroupRepository commonGroupRepository, DataFieldMapper dataFieldMapper,
                                 ReferenceDataRepository referenceDataRepository, StandardExtraRepository standardExtraRepository) {
        this.commonGroupRepository = commonGroupRepository;
        this.dataFieldMapper = dataFieldMapper;
        this.referenceDataRepository = referenceDataRepository;
        this.standardExtraRepository = standardExtraRepository;
    }

    @Override
    public List<FieldStandardExportDTO> export(DataFieldDTO dto) {
        //按条件导出标准，获取分组id，并导出分组数据
        if (StringUtils.isNotEmpty(dto.getExportIds())) {
            //导出指定标准
            List<Long> fieldIds = Arrays.stream(dto.getExportIds().split(COMMA)).map(Long::parseLong).collect(Collectors.toList());
            dto.setFieldIds(fieldIds);
            //获取数据标准
            List<DataFieldDTO> dataFields = dataFieldMapper.list(dto);
            List<Long> groupIds = dataFields.stream().map(DataFieldDTO::getGroupId).collect(Collectors.toList());
            //获取分组数据
            List<CommonGroup> commonGroups = commonGroupRepository.selectByCondition(Condition.builder(CommonGroup.class)
                    .andWhere(Sqls.custom()
                            .andIn(CommonGroup.FIELD_GROUP_ID, groupIds))
                    .build());
            return exportFieldStandard(commonGroups, dataFields);
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
                List<DataFieldDTO> dataFields = dataFieldMapper.list(dto);
                return exportFieldStandard(commonGroups, dataFields);
            }
        } else {
            //导出全部分组和符合条件数据标准数据
            return exportAll(dto);
        }
    }

    private List<FieldStandardExportDTO> exportAll(DataFieldDTO dto) {
        //全部分组条件导出
        //添加查询所有父分组 并排序导出保证导入准确性
        List<CommonGroup> commonGroups = commonGroupRepository.selectByCondition(Condition.builder(CommonGroup.class)
                .andWhere(Sqls.custom()
                        .andEqualTo(CommonGroup.FIELD_TENANT_ID, dto.getTenantId())
                        .andEqualTo(CommonGroup.FIELD_PROJECT_ID, dto.getProjectId())
                        .andEqualTo(CommonGroup.FIELD_GROUP_TYPE, FIELD_STANDARD))
                .build());
        if (CollectionUtils.isEmpty(commonGroups)) {
            return new ArrayList<>();
        }
        Long[] groupIds = commonGroups.stream().map(CommonGroup::getGroupId).toArray(Long[]::new);
        dto.setGroupArrays(groupIds);
        //按条件查询标准数据
        List<DataFieldDTO> dataFieldDTOList = dataFieldMapper.list(dto);
        return exportFieldStandard(commonGroups, dataFieldDTOList);
    }

    public  List<FieldStandardExportDTO> exportFieldStandard(List<CommonGroup> commonGroups, List<DataFieldDTO> dataFieldDTOList) {
        if (CollectionUtils.isNotEmpty(dataFieldDTOList)) {
            for (DataFieldDTO dataFieldDTO : dataFieldDTOList) {
                if (PlanConstant.StandardValueType.REFERENCE_DATA.equals(dataFieldDTO.getValueType()) && StringUtils.isNotBlank(dataFieldDTO.getValueRange())) {
                    long referenceDataId = Long.parseLong(dataFieldDTO.getValueRange());
                    ReferenceDataDTO referenceDataDTO = referenceDataRepository.selectDTOByPrimaryKey(referenceDataId);
                    if (Objects.nonNull(referenceDataDTO)) {
                        dataFieldDTO.setValueRange(referenceDataDTO.getDataCode());
                    }

                }
                // 设置其附加信息
                List<StandardExtraDTO> standardExtraDTOS = standardExtraRepository.selectDTOByCondition(Condition.builder(StandardExtra.class)
                        .andWhere(Sqls.custom()
                                .andEqualTo(StandardExtra.FIELD_STANDARD_ID, dataFieldDTO.getFieldId())
                                .andEqualTo(StandardExtra.FIELD_STANDARD_TYPE, FIELD)
                                .andEqualTo(StandardExtra.FIELD_TENANT_ID, dataFieldDTO.getTenantId()))
                        .build());
                StringBuilder extraStr = new StringBuilder();
                for (StandardExtraDTO extraDTO : standardExtraDTOS) {
                    extraStr.append(String.format("{%s:%s};", extraDTO.getExtraKey(), extraDTO.getExtraValue()));
                }
                if (extraStr.length() > 0) {
                    // 删除最后的分号
                    extraStr.deleteCharAt(extraStr.length()  - 1);
                }
                dataFieldDTO.setStandardExtraStr(extraStr.toString());
            }
        }
        List<FieldStandardExportDTO> fieldStandardExportDTOList = new ArrayList<>();
        commonGroups.forEach(commonGroup -> {
            FieldStandardExportDTO fieldStandardExportDTO = new FieldStandardExportDTO();
            BeanUtils.copyProperties(commonGroup, fieldStandardExportDTO);
            int i = fieldStandardExportDTO.getGroupPath().lastIndexOf("/");
            if (i > 0) {
                fieldStandardExportDTO.setParentGroupPath(fieldStandardExportDTO.getGroupPath().substring(0, i));
            }
            fieldStandardExportDTOList.add(fieldStandardExportDTO);
        });
        //解密
        ExportUtils.decryptFieldStandard(dataFieldDTOList);
        fieldStandardExportDTOList.get(0).setDataFieldDTOList(dataFieldDTOList);
        return fieldStandardExportDTOList;
    }

}
