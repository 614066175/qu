package com.hand.hdsp.quality.domain.repository;

import com.hand.hdsp.core.base.ProxySelf;
import com.hand.hdsp.core.base.repository.BaseRepository;
import com.hand.hdsp.quality.api.dto.BatchResultRuleDTO;
import com.hand.hdsp.quality.domain.entity.BatchResultRule;

import java.util.List;

/**
 * <p>批数据方案结果表-规则信息资源库</p>
 *
 * @author feng.liu01@hand-china.com 2020-03-24 16:19:52
 */
public interface BatchResultRuleRepository extends BaseRepository<BatchResultRule, BatchResultRuleDTO>, ProxySelf<BatchResultRuleRepository> {

    /**
     * 查询出各级规则错误信息
     *
     * @return BatchResultRuleDTO
     */
    List<BatchResultRuleDTO> listRuleError(BatchResultRuleDTO batchResultRuleDTO);

}
