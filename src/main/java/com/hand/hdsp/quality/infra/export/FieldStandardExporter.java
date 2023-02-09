package com.hand.hdsp.quality.infra.export;

import com.hand.hdsp.core.domain.entity.CommonGroup;
import com.hand.hdsp.core.domain.repository.CommonGroupRepository;
import com.hand.hdsp.quality.api.dto.DataFieldDTO;
import com.hand.hdsp.quality.infra.export.dto.FieldStandardExportDTO;
import com.hand.hdsp.quality.infra.mapper.DataFieldMapper;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.hzero.mybatis.domian.Condition;
import org.hzero.mybatis.util.Sqls;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static com.hand.hdsp.core.infra.constant.CommonGroupConstants.GroupType.FIELD_STANDARD;
import static org.hzero.core.util.StringPool.COMMA;

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

    public FieldStandardExporter(CommonGroupRepository commonGroupRepository, DataFieldMapper dataFieldMapper) {
        this.commonGroupRepository = commonGroupRepository;
        this.dataFieldMapper = dataFieldMapper;
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
