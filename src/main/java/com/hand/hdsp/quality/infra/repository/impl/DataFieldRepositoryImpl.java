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
import com.hand.hdsp.quality.infra.mapper.DataStandardMapper;
import org.apache.commons.collections4.CollectionUtils;
import org.hzero.mybatis.domian.Condition;
import org.hzero.mybatis.helper.DataSecurityHelper;
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

    private final DataStandardMapper dataStandardMapper;

    public DataFieldRepositoryImpl(StandardGroupRepository standardGroupRepository,
                                   DataFieldMapper dataFieldMapper,
                                   DataStandardMapper dataStandardMapper) {
        this.standardGroupRepository = standardGroupRepository;
        this.dataFieldMapper = dataFieldMapper;
        this.dataStandardMapper = dataStandardMapper;
    }

    @Override
    public void batchImport(List<DataFieldDTO> dataFieldDTOList) {
        List<DataFieldDTO> importDataFieldDTOList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(dataFieldDTOList)) {
            int len = dataFieldDTOList.size();
            for (int i = 0; i < len; i++) {
                DataFieldDTO curDataFieldDTO = dataFieldDTOList.get(i);
                Condition condition = Condition.builder(DataField.class).andWhere(
                        Sqls.custom()
                                .andEqualTo(DataField.FIELD_FIELD_NAME, curDataFieldDTO.getFieldName())
                                .andEqualTo(DataField.FIELD_TENANT_ID, curDataFieldDTO.getTenantId())
                ).build();
                List<DataField> dataFields = selectByCondition(condition);
                if (CollectionUtils.isNotEmpty(dataFields)) {
                    return;
                }
                Long chargeTenantId = dataFieldMapper.selectTenantIdByChargeName(curDataFieldDTO.getChargeName());
                if (curDataFieldDTO.getTenantId().compareTo(chargeTenantId) != 0) {
                    return;
                }
                Long chargeId = dataFieldMapper.selectIdByChargeName(curDataFieldDTO.getChargeName());
                if (Objects.isNull(chargeId)) {
                    return;
                }
                String chargeDeptName = dataFieldMapper.selectChargeDeptNameById(curDataFieldDTO.getChargeDeptId());
                // 如果开启了加密
                if (isEnableDataSecurity(curDataFieldDTO.getTenantId())) {
                    // 就解密
                    chargeDeptName = DataSecurityHelper.decrypt(chargeDeptName);
                }

                // 把查询验证后的数据设置进去
                curDataFieldDTO.setChargeId(chargeId);
                curDataFieldDTO.setChargeDeptName(chargeDeptName);
                List<StandardGroupDTO> standardGroupDTOList = standardGroupRepository.selectDTOByCondition(Condition.builder(StandardGroup.class)
                        .andWhere(Sqls.custom()
                                .andEqualTo(StandardGroup.FIELD_GROUP_CODE, curDataFieldDTO.getGroupCode())
                                .andEqualTo(StandardGroup.FIELD_TENANT_ID, curDataFieldDTO.getTenantId()))
                        .build());
                if (CollectionUtils.isNotEmpty(standardGroupDTOList)) {
                    curDataFieldDTO.setGroupId(standardGroupDTOList.get(0).getGroupId());
                } else {
                    //创建分组
                    StandardGroupDTO standardGroupDTO = StandardGroupDTO.builder()
                            .groupCode(curDataFieldDTO.getGroupCode())
                            .groupName(curDataFieldDTO.getGroupName())
                            .groupDesc(curDataFieldDTO.getStandardDesc())
                            .standardType(StandardConstant.StandardType.FIELD)
                            .tenantId(curDataFieldDTO.getTenantId())
                            .build();
                    standardGroupRepository.insertDTOSelective(standardGroupDTO);
                    curDataFieldDTO.setGroupId(standardGroupDTO.getGroupId());
                }
                importDataFieldDTOList.add(curDataFieldDTO);
                curDataFieldDTO.setStandardStatus(CREATE);
            }
        }
        batchInsertDTOSelective(importDataFieldDTOList);
    }

    @Override
    public boolean isEnableDataSecurity(Long tenantId) {
        return dataStandardMapper.isEncrypt(tenantId) == 1;
    }
}