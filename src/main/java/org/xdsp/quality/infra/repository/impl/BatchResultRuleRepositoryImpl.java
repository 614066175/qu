package org.xdsp.quality.infra.repository.impl;

import org.springframework.stereotype.Component;
import org.xdsp.core.base.repository.impl.BaseRepositoryImpl;
import org.xdsp.quality.api.dto.BatchResultRuleDTO;
import org.xdsp.quality.domain.entity.BatchResultRule;
import org.xdsp.quality.domain.repository.BatchResultRuleRepository;

/**
 * <p>批数据方案结果表-规则信息资源库实现</p>
 *
 * @author feng.liu01@hand-china.com 2020-03-24 16:19:52
 */
@Component
public class BatchResultRuleRepositoryImpl extends BaseRepositoryImpl<BatchResultRule, BatchResultRuleDTO> implements BatchResultRuleRepository {
}
