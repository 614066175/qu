package com.hand.hdsp.quality.infra.repository.impl;

import static com.hand.hdsp.quality.infra.constant.StandardConstant.StandardType.DATA;
import static com.hand.hdsp.quality.infra.constant.StandardConstant.StandardType.FIELD;
import static com.hand.hdsp.quality.infra.constant.StandardConstant.Status.CREATE;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.hand.hdsp.core.base.repository.impl.BaseRepositoryImpl;
import com.hand.hdsp.quality.api.dto.DataStandardDTO;
import com.hand.hdsp.quality.api.dto.StandardGroupDTO;
import com.hand.hdsp.quality.domain.entity.DataStandard;
import com.hand.hdsp.quality.domain.entity.StandardGroup;
import com.hand.hdsp.quality.domain.repository.DataStandardRepository;
import com.hand.hdsp.quality.domain.repository.StandardGroupRepository;
import com.hand.hdsp.quality.infra.constant.StandardConstant;
import com.hand.hdsp.quality.infra.mapper.DataStandardMapper;
import io.choerodon.core.exception.CommonException;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.logging.log4j.util.Strings;
import org.hzero.mybatis.domian.Condition;
import org.hzero.mybatis.helper.DataSecurityHelper;
import org.hzero.mybatis.util.Sqls;
import org.springframework.stereotype.Component;

/**
 * <p>数据标准表资源库实现</p>
 *
 * @author guoliangli01@hand-china.com 2020-11-27 14:36:44
 */
@Component
public class DataStandardRepositoryImpl extends BaseRepositoryImpl<DataStandard, DataStandardDTO> implements DataStandardRepository {

    private final DataStandardMapper dataStandardMapper;

    private final StandardGroupRepository standardGroupRepository;

    public DataStandardRepositoryImpl(DataStandardMapper dataStandardMapper, StandardGroupRepository standardGroupRepository) {
        this.dataStandardMapper = dataStandardMapper;
        this.standardGroupRepository = standardGroupRepository;
    }

    @Override
    public boolean batchImport(List<DataStandardDTO> dataStandardDTOList) {
        List<DataStandardDTO> importDataStandardDTOList = new ArrayList<>();
        //处理得到待导入的集合
        if (CollectionUtils.isNotEmpty(dataStandardDTOList)) {
            for (DataStandardDTO dataStandardDTO : dataStandardDTOList) {//判断数据标准是否已经存在,存在则不导入
                List<DataStandard> dataStandards;
                dataStandards = selectByCondition(Condition.builder(DataStandard.class)
                        .andWhere(Sqls.custom()
                                .andEqualTo(DataStandard.FIELD_STANDARD_CODE, dataStandardDTO.getStandardCode())
                                .andEqualTo(DataStandard.FIELD_TENANT_ID, dataStandardDTO.getTenantId())
                        )
                        .build());
                if (CollectionUtils.isNotEmpty(dataStandards)) {
                    throw new CommonException("标准编码已存在");
                }
                dataStandards = selectByCondition(Condition.builder(DataStandard.class)
                        .andWhere(Sqls.custom()
                                .andEqualTo(DataStandard.FIELD_STANDARD_NAME, dataStandardDTO.getStandardCode())
                                .andEqualTo(DataStandard.FIELD_TENANT_ID, dataStandardDTO.getTenantId())
                        )
                        .build());
                if (CollectionUtils.isNotEmpty(dataStandards)) {
                    throw new CommonException("标准名称已存在");
                }
                List<Long> chargeId = dataStandardMapper.selectIdByChargeName(dataStandardDTO.getChargeName(), dataStandardDTO.getTenantId());
                if (Strings.isNotEmpty(dataStandardDTO.getChargeDeptName())) {
                    String chargeDeptName = dataStandardDTO.getChargeDeptName();
                    if (DataSecurityHelper.isTenantOpen()) {
                        chargeDeptName = DataSecurityHelper.encrypt(chargeDeptName);
                    }
                    List<Long> chargeDeptId = dataStandardMapper.selectIdByChargeDeptName(chargeDeptName, dataStandardDTO.getTenantId());
                    if (CollectionUtils.isEmpty(chargeDeptId)) {
                        throw new CommonException("责任部门不存在");
                    }
                    dataStandardDTO.setChargeDeptId(chargeDeptId.get(0));
                }
                dataStandardDTO.setChargeId(chargeId.get(0));
                List<StandardGroupDTO> standardGroupDTOS = standardGroupRepository.selectDTOByCondition(Condition.builder(StandardGroup.class)
                        .andWhere(Sqls.custom()
                                .andEqualTo(StandardGroup.FIELD_GROUP_CODE, dataStandardDTO.getGroupCode())
                                .andEqualTo(StandardGroup.FIELD_TENANT_ID, dataStandardDTO.getTenantId())
                                .andEqualTo(StandardGroup.FIELD_STANDARD_TYPE, DATA))
                        .build());
                if (CollectionUtils.isNotEmpty(standardGroupDTOS)) {
                    dataStandardDTO.setGroupId(standardGroupDTOS.get(0).getGroupId());
                } else {
                    //创建分组
                    StandardGroupDTO standardGroupDTO = StandardGroupDTO.builder()
                            .groupCode(dataStandardDTO.getGroupCode())
                            .groupName(dataStandardDTO.getGroupName())
                            .groupDesc(dataStandardDTO.getGroupDesc())
                            .standardType(StandardConstant.StandardType.DATA)
                            .tenantId(dataStandardDTO.getTenantId())
                            .build();
                    standardGroupRepository.insertDTOSelective(standardGroupDTO);
                    dataStandardDTO.setGroupId(standardGroupDTO.getGroupId());
                }
                importDataStandardDTOList.add(dataStandardDTO);
                dataStandardDTO.setStandardStatus(CREATE);
            }
        }
        batchInsertDTOSelective(importDataStandardDTOList);
        return true;
    }
}
