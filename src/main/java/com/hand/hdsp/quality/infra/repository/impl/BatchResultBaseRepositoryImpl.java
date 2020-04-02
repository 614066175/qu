package com.hand.hdsp.quality.infra.repository.impl;

import java.util.List;

import com.hand.hdsp.core.base.repository.impl.BaseRepositoryImpl;
import com.hand.hdsp.quality.api.dto.BatchResultBaseDTO;
import com.hand.hdsp.quality.domain.entity.BatchResultBase;
import com.hand.hdsp.quality.domain.repository.BatchResultBaseRepository;
import com.hand.hdsp.quality.infra.mapper.BatchResultRuleMapper;
import org.hzero.mybatis.domian.Condition;
import org.hzero.mybatis.util.Sqls;
import org.springframework.stereotype.Component;

/**
 * <p>批数据方案结果表-表信息资源库实现</p>
 *
 * @author feng.liu01@hand-china.com 2020-03-24 16:19:52
 */
@Component
public class BatchResultBaseRepositoryImpl extends BaseRepositoryImpl<BatchResultBase, BatchResultBaseDTO> implements BatchResultBaseRepository {

    private final BatchResultRuleMapper batchResultRuleMapper;

    public BatchResultBaseRepositoryImpl(BatchResultRuleMapper batchResultRuleMapper) {
        this.batchResultRuleMapper = batchResultRuleMapper;
    }

    @Override
    public List<BatchResultBaseDTO> listResultBase(BatchResultBaseDTO batchResultBaseDTO) {
        List<BatchResultBaseDTO> batchResultBaseDTOS = this.selectDTOByCondition(
                Condition.builder(BatchResultBase.class)
                        .where(Sqls.custom()
                                .andEqualTo(BatchResultBase.FIELD_RESULT_ID, batchResultBaseDTO.getResultId(), true)
                                .andEqualTo(BatchResultBase.FIELD_TENANT_ID, batchResultBaseDTO.getTenantId(), true))
                        .build()
        );
        if (!batchResultBaseDTOS.isEmpty()){
            batchResultBaseDTOS.stream().forEach( s ->{
                s.setResultWaringVOS(batchResultRuleMapper.selectWaringLevel(batchResultBaseDTO.getTenantId(),s.getTableName()));
            });
        }
        return batchResultBaseDTOS;
    }
}
