package com.hand.hdsp.quality.infra.export;

import com.hand.hdsp.core.domain.entity.CommonGroup;
import com.hand.hdsp.core.domain.repository.CommonGroupRepository;
import com.hand.hdsp.quality.api.dto.DataStandardDTO;
import com.hand.hdsp.quality.infra.export.dto.DataStandardExportDTO;
import com.hand.hdsp.quality.infra.mapper.DataStandardMapper;
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

import static com.hand.hdsp.core.infra.constant.CommonGroupConstants.GroupType.DATA_STANDARD;
import static org.hzero.core.util.StringPool.COMMA;

/**
 * @Title: DataStandardExporter
 * @Description:
 * @author: lgl
 * @date: 2023/2/8 14:06
 */
@Component
public class DataStandardExporter implements Exporter<DataStandardDTO, List<DataStandardExportDTO>> {
    private final CommonGroupRepository commonGroupRepository;
    private final DataStandardMapper dataStandardMapper;

    public DataStandardExporter(CommonGroupRepository commonGroupRepository, DataStandardMapper dataStandardMapper) {
        this.commonGroupRepository = commonGroupRepository;
        this.dataStandardMapper = dataStandardMapper;
    }

    @Override
    public List<DataStandardExportDTO> export(DataStandardDTO dto) {
        //按条件导出标准，获取分组id，并导出分组数据
        if (StringUtils.isNotEmpty(dto.getExportIds())) {
            //导出指定标准
            List<Long> standardIds = Arrays.stream(dto.getExportIds().split(COMMA)).map(Long::parseLong).collect(Collectors.toList());
            dto.setStandardIds(standardIds);
            //获取数据标准
            List<DataStandardDTO> dataStandards = dataStandardMapper.list(dto);
            List<Long> groupIds = dataStandards.stream().map(DataStandardDTO::getGroupId).collect(Collectors.toList());
            //获取分组数据
            List<CommonGroup> commonGroups = commonGroupRepository.selectByCondition(Condition.builder(CommonGroup.class)
                    .andWhere(Sqls.custom()
                            .andIn(CommonGroup.FIELD_GROUP_ID, groupIds))
                    .build());
            return exportDataStandard(commonGroups, dataStandards);
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
                List<DataStandardDTO> dataStandards = dataStandardMapper.list(dto);
                return exportDataStandard(commonGroups, dataStandards);
            }
        } else {
            //导出全部分组和符合条件数据标准数据
            return exportAll(dto);
        }
    }

    private List<DataStandardExportDTO> exportAll(DataStandardDTO dto) {
        //全部分组条件导出
        //添加查询所有父分组 并排序导出保证导入准确性
        List<CommonGroup> commonGroups = commonGroupRepository.selectByCondition(Condition.builder(CommonGroup.class)
                .andWhere(Sqls.custom()
                        .andEqualTo(CommonGroup.FIELD_TENANT_ID, dto.getTenantId())
                        .andEqualTo(CommonGroup.FIELD_PROJECT_ID, dto.getProjectId())
                        .andEqualTo(CommonGroup.FIELD_GROUP_TYPE, DATA_STANDARD))
                .build());
        if (CollectionUtils.isEmpty(commonGroups)) {
            return new ArrayList<>();
        }
        Long[] groupIds = commonGroups.stream().map(CommonGroup::getGroupId).toArray(Long[]::new);
        dto.setGroupArrays(groupIds);
        //按条件查询标准数据
        List<DataStandardDTO> dataStandardDTOList = dataStandardMapper.list(dto);
        return exportDataStandard(commonGroups, dataStandardDTOList);
    }

    public  List<DataStandardExportDTO> exportDataStandard(List<CommonGroup> commonGroups, List<DataStandardDTO> dataStandardDTOList) {
        List<DataStandardExportDTO> dataStandardExportDTOS = new ArrayList<>();
        commonGroups.forEach(commonGroup -> {
            DataStandardExportDTO dataStandardExportDTO = new DataStandardExportDTO();
            BeanUtils.copyProperties(commonGroup, dataStandardExportDTO);
            int i = dataStandardExportDTO.getGroupPath().lastIndexOf("/");
            if (i > 0) {
                dataStandardExportDTO.setParentGroupPath(dataStandardExportDTO.getGroupPath().substring(0, i));
            }
            dataStandardExportDTOS.add(dataStandardExportDTO);
        });
        //解密
        ExportUtils.decryptDataStandard(dataStandardDTOList);
        dataStandardExportDTOS.get(0).setDataStandardDTOList(dataStandardDTOList);
        return dataStandardExportDTOS;
    }

}
