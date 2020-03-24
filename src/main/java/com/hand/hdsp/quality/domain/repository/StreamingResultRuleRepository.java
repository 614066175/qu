package com.hand.hdsp.quality.domain.repository;

import com.hand.hdsp.core.base.ProxySelf;
import com.hand.hdsp.core.base.repository.BaseRepository;
import com.hand.hdsp.quality.api.dto.StreamingResultRuleDTO;
import com.hand.hdsp.quality.domain.entity.StreamingResultRule;

/**
 * <p>实时数据方案结果表-规则信息资源库</p>
 *
 * @author feng.liu01@hand-china.com 2020-03-24 16:19:50
 */
public interface StreamingResultRuleRepository extends BaseRepository<StreamingResultRule, StreamingResultRuleDTO>, ProxySelf<StreamingResultRuleRepository> {

}
