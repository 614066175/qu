package org.xdsp.quality.domain.repository;

import org.xdsp.core.base.ProxySelf;
import org.xdsp.core.base.repository.BaseRepository;
import org.xdsp.quality.api.dto.BatchResultRuleDTO;
import org.xdsp.quality.domain.entity.BatchResultRule;

/**
 * <p>批数据方案结果表-规则信息资源库</p>
 *
 * @author feng.liu01@hand-china.com 2020-03-24 16:19:52
 */
public interface BatchResultRuleRepository extends BaseRepository<BatchResultRule, BatchResultRuleDTO>, ProxySelf<BatchResultRuleRepository> {

}
