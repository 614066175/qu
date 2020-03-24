package com.hand.hdsp.quality.infra.repository.impl;

import com.hand.hdsp.core.base.repository.impl.BaseRepositoryImpl;
import com.hand.hdsp.quality.api.dto.BatchResultRuleDTO;
import com.hand.hdsp.quality.domain.entity.BatchResultRule;
import com.hand.hdsp.quality.domain.repository.BatchResultRuleRepository;
import org.springframework.stereotype.Component;

/**
 * <p>批数据方案结果表-规则信息资源库实现</p>
 *
 * @author feng.liu01@hand-china.com 2020-03-24 16:19:52
 */
@Component
public class BatchResultRuleRepositoryImpl extends BaseRepositoryImpl<BatchResultRule, BatchResultRuleDTO> implements BatchResultRuleRepository {

}
