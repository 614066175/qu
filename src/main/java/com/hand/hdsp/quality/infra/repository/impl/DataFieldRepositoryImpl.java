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
import io.choerodon.core.exception.CommonException;
import org.apache.commons.collections4.CollectionUtils;
import org.hzero.mybatis.domian.Condition;
import org.hzero.mybatis.helper.DataSecurityHelper;
import org.hzero.mybatis.util.Sqls;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.hand.hdsp.quality.infra.constant.StandardConstant.StandardType.FIELD;
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
    public boolean batchImport(List<DataFieldDTO> dataFieldDTOList) {
        List<DataFieldDTO> importDataFieldDTOList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(dataFieldDTOList)) {
            for (DataFieldDTO dataFieldDTO : dataFieldDTOList) {
                Condition condition = Condition.builder(DataField.class).andWhere(
                        Sqls.custom()
                                .andEqualTo(DataField.FIELD_FIELD_NAME, dataFieldDTO.getFieldName())
                                .andEqualTo(DataField.FIELD_TENANT_ID, dataFieldDTO.getTenantId())
                ).build();
                List<DataField> dataFields = selectByCondition(condition);
                if (CollectionUtils.isNotEmpty(dataFields)) {
                    throw new CommonException("字段名称已存在");
                }
                List<Long> chargeId = dataFieldMapper.selectIdByChargeName(dataFieldDTO.getChargeName(), dataFieldDTO.getTenantId());
                if (CollectionUtils.isEmpty(chargeId)) {
                   throw new CommonException("责任人不存在");
                }
                //判断当前租户是否开启了数据加密
                String chargeDeptName = dataFieldDTO.getChargeDeptName();
                if (DataSecurityHelper.isTenantOpen()) {
                    chargeDeptName = DataSecurityHelper.encrypt(chargeDeptName);
                }
                List<Long> chargeDeptId = dataFieldMapper.selectIdByChargeDeptName(chargeDeptName, dataFieldDTO.getTenantId());
                if (CollectionUtils.isEmpty(chargeDeptId)) {
                    throw new CommonException("责任部门不存在");
                }
                // 把查询验证后的数据设置进去
                dataFieldDTO.setChargeId(chargeId.get(0));
                dataFieldDTO.setChargeDeptId(chargeDeptId.get(0));
                //索引group_code+type+tenant_id
                List<StandardGroupDTO> standardGroupDTOList = standardGroupRepository.selectDTOByCondition(Condition.builder(StandardGroup.class)
                        .andWhere(Sqls.custom()
                                .andEqualTo(StandardGroup.FIELD_GROUP_CODE, dataFieldDTO.getGroupCode())
                                .andEqualTo(StandardGroup.FIELD_TENANT_ID, dataFieldDTO.getTenantId())
                                .andEqualTo(StandardGroup.FIELD_STANDARD_TYPE,FIELD))
                        .build());
                if (CollectionUtils.isNotEmpty(standardGroupDTOList)) {
                    dataFieldDTO.setGroupId(standardGroupDTOList.get(0).getGroupId());
                } else {
                    //创建分组
                    StandardGroupDTO standardGroupDTO = StandardGroupDTO.builder()
                            .groupCode(dataFieldDTO.getGroupCode())
                            .groupName(dataFieldDTO.getGroupName())
                            .groupDesc(dataFieldDTO.getStandardDesc())
                            .standardType(FIELD)
                            .tenantId(dataFieldDTO.getTenantId())
                            .build();
                    standardGroupRepository.insertDTOSelective(standardGroupDTO);
                    dataFieldDTO.setGroupId(standardGroupDTO.getGroupId());
                }
                importDataFieldDTOList.add(dataFieldDTO);
                dataFieldDTO.setStandardStatus(CREATE);
            }
        }
        batchInsertDTOSelective(importDataFieldDTOList);
        return true;
    }

    @Override
    public boolean isEnableDataSecurity(Long tenantId) {
        return dataStandardMapper.isEncrypt(tenantId) == 1;
    }
}