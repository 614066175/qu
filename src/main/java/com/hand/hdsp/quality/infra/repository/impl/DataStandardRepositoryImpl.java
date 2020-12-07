package com.hand.hdsp.quality.infra.repository.impl;

import java.util.ArrayList;
import java.util.List;

import com.hand.hdsp.core.base.repository.impl.BaseRepositoryImpl;
import com.hand.hdsp.quality.api.dto.DataStandardDTO;
import com.hand.hdsp.quality.domain.entity.DataStandard;
import com.hand.hdsp.quality.domain.repository.DataStandardRepository;
import com.hand.hdsp.quality.infra.mapper.DataStandardMapper;
import org.apache.commons.collections4.CollectionUtils;
import org.hzero.mybatis.domian.Condition;
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

    public DataStandardRepositoryImpl(DataStandardMapper dataStandardMapper) {
        this.dataStandardMapper = dataStandardMapper;
    }

    @Override
    public void batchImport(List<DataStandardDTO> dataStandardDTOList) {
        List<DataStandardDTO> importDataStandardDTOList = new ArrayList<>();
        //处理得到待导入的集合
        if(CollectionUtils.isNotEmpty(dataStandardDTOList)){
            dataStandardDTOList.forEach(dataStandardDTO -> {
                //判断数据标准是否已经存在,存在则不导入
                List<DataStandard> dataStandards;
                dataStandards = selectByCondition(Condition.builder(DataStandard.class)
                        .andWhere(Sqls.custom()
                                .andEqualTo(DataStandard.FIELD_STANDARD_CODE, dataStandardDTO.getStandardCode())
                                .andEqualTo(DataStandard.FIELD_TENANT_ID, dataStandardDTO.getTenantId())
                        )
                        .build());
                if(CollectionUtils.isNotEmpty(dataStandards)){
                    return;
                }
                dataStandards = selectByCondition(Condition.builder(DataStandard.class)
                        .andWhere(Sqls.custom()
                                .andEqualTo(DataStandard.FIELD_STANDARD_NAME, dataStandardDTO.getStandardCode())
                                .andEqualTo(DataStandard.FIELD_TENANT_ID, dataStandardDTO.getTenantId())
                        )
                        .build());
                if(CollectionUtils.isNotEmpty(dataStandards)){
                    return;
                }
                importDataStandardDTOList.add(dataStandardDTO);
            });
        }
        batchInsertDTOSelective(importDataStandardDTOList);
    }
}
