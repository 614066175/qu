package com.hand.hdsp.quality.infra.export;

import com.hand.hdsp.core.domain.entity.CommonGroup;
import com.hand.hdsp.core.domain.repository.CommonGroupRepository;
import com.hand.hdsp.quality.api.dto.NameStandardDTO;
import com.hand.hdsp.quality.infra.export.dto.NameStandardExportDTO;
import com.hand.hdsp.quality.infra.mapper.NameStandardMapper;
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

import static com.hand.hdsp.core.infra.constant.CommonGroupConstants.GroupType.NAME_STANDARD;
import static org.hzero.core.util.StringPool.COMMA;

/**
 * @Title: FieldStandardExporter
 * @Description:
 * @author: lgl
 * @date: 2023/2/8 14:15
 */
@Component
public class NameStandardExporter implements Exporter<NameStandardDTO, List<NameStandardExportDTO>> {
    private final CommonGroupRepository commonGroupRepository;
    private final NameStandardMapper nameStandardMapper;

    public NameStandardExporter(CommonGroupRepository commonGroupRepository, NameStandardMapper nameStandardMapper) {
        this.commonGroupRepository = commonGroupRepository;
        this.nameStandardMapper = nameStandardMapper;
    }

    @Override
    public List<NameStandardExportDTO> export(NameStandardDTO dto) {
        //按条件导出标准，获取分组id，并导出分组数据
        if (StringUtils.isNotEmpty(dto.getExportIds())) {
            //导出指定标准
            List<Long> standardIds = Arrays.stream(dto.getExportIds().split(COMMA)).map(Long::parseLong).collect(Collectors.toList());
            dto.setStandardIds(standardIds);
            //获取数据标准
            List<NameStandardDTO> dataStandards = nameStandardMapper.list(dto);
            List<Long> groupIds = dataStandards.stream().map(NameStandardDTO::getGroupId).collect(Collectors.toList());
            //获取分组数据
            List<CommonGroup> commonGroups = commonGroupRepository.selectByCondition(Condition.builder(CommonGroup.class)
                    .andWhere(Sqls.custom()
                            .andIn(CommonGroup.FIELD_GROUP_ID, groupIds))
                    .build());
            return exportNameStandard(commonGroups, dataStandards);
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
                List<NameStandardDTO> dataStandards = nameStandardMapper.list(dto);
                return exportNameStandard(commonGroups, dataStandards);
            }
        } else {
            //导出全部分组和符合条件数据标准数据
            return exportAll(dto);
        }
    }

    private List<NameStandardExportDTO> exportAll(NameStandardDTO dto) {
        //全部分组条件导出
        //添加查询所有父分组 并排序导出保证导入准确性
        List<CommonGroup> commonGroups = commonGroupRepository.selectByCondition(Condition.builder(CommonGroup.class)
                .andWhere(Sqls.custom()
                        .andEqualTo(CommonGroup.FIELD_TENANT_ID, dto.getTenantId())
                        .andEqualTo(CommonGroup.FIELD_PROJECT_ID, dto.getProjectId())
                        .andEqualTo(CommonGroup.FIELD_GROUP_TYPE, NAME_STANDARD))
                .build());
        if (CollectionUtils.isEmpty(commonGroups)) {
            return new ArrayList<>();
        }
        Long[] groupIds = commonGroups.stream().map(CommonGroup::getGroupId).toArray(Long[]::new);
        dto.setGroupArrays(groupIds);
        //按条件查询标准数据
        List<NameStandardDTO> NameStandardDTOList = nameStandardMapper.list(dto);
        return exportNameStandard(commonGroups, NameStandardDTOList);
    }

    public  List<NameStandardExportDTO> exportNameStandard(List<CommonGroup> commonGroups, List<NameStandardDTO> nameStandardDTOList) {
        List<NameStandardExportDTO> nameStandardExportDTOS = new ArrayList<>();
        commonGroups.forEach(commonGroup -> {
            NameStandardExportDTO nameStandardExportDTO = new NameStandardExportDTO();
            BeanUtils.copyProperties(commonGroup, nameStandardExportDTO);
            int i = nameStandardExportDTO.getGroupPath().lastIndexOf("/");
            if (i > 0) {
                nameStandardExportDTO.setParentGroupPath(nameStandardExportDTO.getGroupPath().substring(0, i));
            }
            nameStandardExportDTOS.add(nameStandardExportDTO);
        });
        //解密
        ExportUtils.decryptNameStandard(nameStandardDTOList);
        nameStandardExportDTOS.get(0).setNameStandardDTOList(nameStandardDTOList);
        return nameStandardExportDTOS;
    }
}
