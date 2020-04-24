package com.hand.hdsp.quality.infra.repository.impl;

import com.hand.hdsp.core.base.repository.impl.BaseRepositoryImpl;
import com.hand.hdsp.quality.api.dto.BatchResultRuleDTO;
import com.hand.hdsp.quality.domain.entity.BatchResultRule;
import com.hand.hdsp.quality.domain.repository.BatchResultRuleRepository;
import com.hand.hdsp.quality.infra.mapper.BatchResultRuleMapper;
import org.hzero.mybatis.domian.Condition;
import org.hzero.mybatis.util.Sqls;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * <p>批数据方案结果表-规则信息资源库实现</p>
 *
 * @author feng.liu01@hand-china.com 2020-03-24 16:19:52
 */
@Component
public class BatchResultRuleRepositoryImpl extends BaseRepositoryImpl<BatchResultRule, BatchResultRuleDTO> implements BatchResultRuleRepository {

    private final BatchResultRuleMapper batchResultRuleMapper;

    public BatchResultRuleRepositoryImpl(BatchResultRuleMapper batchResultRuleMapper) {
        this.batchResultRuleMapper = batchResultRuleMapper;
    }

    @Override
    public List<BatchResultRuleDTO> selectByResultId(Long resultId) {
        return batchResultRuleMapper.selectByResultId(resultId);
    }

    @Override
    public List<BatchResultRuleDTO> listRuleError(BatchResultRuleDTO batchResultRuleDTO) {
        return this.selectDTOByCondition(
                Condition.builder(BatchResultRule.class)
                        .where(Sqls.custom()
                                .andEqualTo(BatchResultRule.FIELD_RESULT_BASE_ID, batchResultRuleDTO.getResultBaseId(), true)
                                .andEqualTo(BatchResultRule.FIELD_TENANT_ID, batchResultRuleDTO.getTenantId(), true)
                                .andLike(BatchResultRule.FIELD_RULE_NAME, batchResultRuleDTO.getRuleName(), true)
                                .andEqualTo(BatchResultRule.FIELD_RULE_TYPE, batchResultRuleDTO.getRuleType(), true))
                        .build()
        );
    }

    @Override
    public List<String> selectWaringLevelByResultId(Long resultId) {
        return batchResultRuleMapper.selectWaringLevelByResultId(resultId);
    }
}
