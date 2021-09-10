package com.hand.hdsp.quality.infra.repository.impl;

import com.hand.hdsp.core.base.repository.impl.BaseRepositoryImpl;
import com.hand.hdsp.quality.api.dto.DataFieldDTO;
import com.hand.hdsp.quality.api.dto.StandardGroupDTO;
import com.hand.hdsp.quality.domain.entity.DataField;
import com.hand.hdsp.quality.domain.entity.StandardGroup;
import com.hand.hdsp.quality.domain.repository.DataFieldRepository;
import com.hand.hdsp.quality.domain.repository.StandardGroupRepository;
import com.hand.hdsp.quality.infra.constant.StandardConstant;
import com.hand.hdsp.quality.infra.mapper.DataFieldMapper;
import org.apache.commons.collections4.CollectionUtils;
import org.hzero.mybatis.domian.Condition;
import org.hzero.mybatis.util.Sqls;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.hand.hdsp.quality.infra.constant.StandardConstant.Status.CREATE;

/**
 * <p>字段标准表资源库实现</p>
 *
 * @author wsl 2020-11-25 15:38:21
 */
@Component
public class DataFieldRepositoryImpl extends BaseRepositoryImpl<DataField, DataFieldDTO> implements DataFieldRepository {

    private final StandardGroupRepository standardGroupRepository;

    private final DataFieldMapper dataFieldMapper;

    public DataFieldRepositoryImpl(StandardGroupRepository standardGroupRepository, DataFieldMapper dataFieldMapper) {
        this.standardGroupRepository = standardGroupRepository;
        this.dataFieldMapper = dataFieldMapper;
    }

    @Override
    public void batchImport(List<DataFieldDTO> dataFieldDTOList) {
        List<DataFieldDTO> importDataFieldDTOList = new ArrayList<>();
        //处理得到待导入的集合
        if (CollectionUtils.isNotEmpty(dataFieldDTOList)) {
            dataFieldDTOList.forEach(dataFieldDTO -> {
                //判断数据标准是否已经存在,存在则不导入
                List<DataField> dataFields;
                dataFields = selectByCondition(Condition.builder(DataField.class)
                        .andWhere(Sqls.custom()
                                .andEqualTo(DataField.FIELD_FIELD_NAME, dataFieldDTO.getFieldName())
                                .andEqualTo(DataField.FIELD_TENANT_ID, dataFieldDTO.getTenantId())
                        )
                        .build());
                if (CollectionUtils.isNotEmpty(dataFields)) {
                    return;
                }
                Long chargeTenantId = dataFieldMapper.selectTenantIdByChargeName(dataFieldDTO.getChargeName());
                if (dataFieldDTO.getTenantId().compareTo(chargeTenantId) != 0) {
                    return;
                }
                Long chargeId = dataFieldMapper.selectIdByChargeName(dataFieldDTO.getChargeName());
                Long chargeDeptId = dataFieldMapper.selectIdByChargeDeptName(dataFieldDTO.getChargeDeptName());
                if (Objects.isNull(chargeId) || Objects.isNull(chargeDeptId)) {
                    return;
                }
                dataFieldDTO.setChargeId(chargeId);
                dataFieldDTO.setChargeDeptId(chargeDeptId);

                List<StandardGroupDTO> standardGroupDTOS = standardGroupRepository.selectDTOByCondition(Condition.builder(StandardGroup.class)
                        .andWhere(Sqls.custom()
                                .andEqualTo(StandardGroup.FIELD_GROUP_CODE, dataFieldDTO.getGroupCode())
                                .andEqualTo(StandardGroup.FIELD_TENANT_ID, dataFieldDTO.getTenantId()))
                        .build());
                if (CollectionUtils.isNotEmpty(standardGroupDTOS)) {
                    dataFieldDTO.setGroupId(standardGroupDTOS.get(0).getGroupId());
                } else {
                    //创建分组
                    StandardGroupDTO standardGroupDTO = StandardGroupDTO.builder()
                            .groupCode(dataFieldDTO.getGroupCode())
                            .groupName(dataFieldDTO.getGroupName())
                            .groupDesc(dataFieldDTO.getStandardDesc())
                            .standardType(StandardConstant.StandardType.FIELD)
                            .tenantId(dataFieldDTO.getTenantId())
                            .build();
                    standardGroupRepository.insertDTOSelective(standardGroupDTO);
                    dataFieldDTO.setGroupId(standardGroupDTO.getGroupId());
                }
                importDataFieldDTOList.add(dataFieldDTO);
                dataFieldDTO.setStandardStatus(CREATE);
            });
        }
        batchInsertDTOSelective(importDataFieldDTOList);
    }
}