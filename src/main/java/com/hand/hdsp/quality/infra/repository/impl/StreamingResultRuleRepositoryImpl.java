package com.hand.hdsp.quality.infra.repository.impl;

import com.hand.hdsp.core.base.repository.impl.BaseRepositoryImpl;
import com.hand.hdsp.quality.api.dto.StreamingResultRuleDTO;
import com.hand.hdsp.quality.domain.entity.StreamingResultRule;
import com.hand.hdsp.quality.domain.repository.StreamingResultRuleRepository;
import org.hzero.mybatis.domian.Condition;
import org.hzero.mybatis.util.Sqls;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * <p>实时数据方案结果表-规则信息资源库实现</p>
 *
 * @author feng.liu01@hand-china.com 2020-03-24 16:19:50
 */
@Component
public class StreamingResultRuleRepositoryImpl extends BaseRepositoryImpl<StreamingResultRule, StreamingResultRuleDTO> implements StreamingResultRuleRepository {

    @Override
    public List<StreamingResultRuleDTO> listResultRule(StreamingResultRuleDTO streamingResultRuleDTO) {
        return this.selectDTOByCondition(
                Condition.builder(StreamingResultRule.class)
                        .where(Sqls.custom()
                                .andEqualTo(StreamingResultRule.FIELD_RESULT_BASE_ID, streamingResultRuleDTO.getResultBaseId(), true)
                                .andEqualTo(StreamingResultRule.FIELD_PROJECT_ID,streamingResultRuleDTO.getProjectId(),true)
                                .andEqualTo(StreamingResultRule.FIELD_TENANT_ID, streamingResultRuleDTO.getTenantId(), true))
                        .build()
        );
    }
}
