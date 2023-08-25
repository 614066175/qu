package org.xdsp.quality.infra.repository.impl;

import io.choerodon.core.exception.CommonException;
import org.apache.commons.collections4.CollectionUtils;
import org.hzero.mybatis.domian.Condition;
import org.hzero.mybatis.util.Sqls;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.xdsp.core.base.repository.impl.BaseRepositoryImpl;
import org.xdsp.quality.api.dto.DataStandardDTO;
import org.xdsp.quality.api.dto.StandardGroupDTO;
import org.xdsp.quality.domain.entity.DataStandard;
import org.xdsp.quality.domain.repository.DataStandardRepository;
import org.xdsp.quality.domain.repository.StandardGroupRepository;
import org.xdsp.quality.infra.constant.StandardConstant;
import org.xdsp.quality.infra.mapper.DataStandardMapper;
import org.xdsp.quality.infra.util.ImportUtil;

import java.util.ArrayList;
import java.util.List;

import static org.xdsp.quality.infra.constant.StandardConstant.Status.CREATE;

/**
 * <p>数据标准表资源库实现</p>
 *
 * @author guoliangli01@hand-china.com 2020-11-27 14:36:44
 */
@Component
public class DataStandardRepositoryImpl extends BaseRepositoryImpl<DataStandard, DataStandardDTO> implements DataStandardRepository {

    private final DataStandardMapper dataStandardMapper;

    private final StandardGroupRepository standardGroupRepository;

    private final ImportUtil importUtil;

    public DataStandardRepositoryImpl(DataStandardMapper dataStandardMapper, StandardGroupRepository standardGroupRepository, ImportUtil importUtil) {
        this.dataStandardMapper = dataStandardMapper;
        this.standardGroupRepository = standardGroupRepository;
        this.importUtil = importUtil;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
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
                //使用工具类获取责任人Id，和责任部门Id
                dataStandardDTO.setChargeDeptId(importUtil.getChargeDeptId(dataStandardDTO.getChargeDeptName(), dataStandardDTO.getTenantId()));
                dataStandardDTO.setChargeId(importUtil.getChargerId(dataStandardDTO.getChargeName(), dataStandardDTO.getTenantId()));

                StandardGroupDTO standardGroupDTO = StandardGroupDTO.builder()
                        .groupCode(dataStandardDTO.getGroupCode())
                        .groupName(dataStandardDTO.getGroupName())
                        .groupDesc(dataStandardDTO.getGroupDesc())
                        .standardType(StandardConstant.StandardType.DATA)
                        .tenantId(dataStandardDTO.getTenantId())
                        .build();
                //有则返回，无则新建
                StandardGroupDTO standardGroup = importUtil.getStandardGroup(standardGroupDTO);
                dataStandardDTO.setGroupId(standardGroup.getGroupId());

                importDataStandardDTOList.add(dataStandardDTO);
                dataStandardDTO.setStandardStatus(CREATE);
            }
        }
        batchInsertDTOSelective(importDataStandardDTOList);
        return true;
    }
}
