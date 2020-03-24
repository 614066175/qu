package com.hand.hdsp.quality.domain.repository;

import com.hand.hdsp.core.base.ProxySelf;
import com.hand.hdsp.core.base.repository.BaseRepository;
import com.hand.hdsp.quality.api.dto.BatchPlanFieldLineDTO;
import com.hand.hdsp.quality.domain.entity.BatchPlanFieldLine;

/**
 * <p>批数据方案-字段规则校验项表资源库</p>
 *
 * @author feng.liu01@hand-china.com 2020-03-24 16:19:53
 */
public interface BatchPlanFieldLineRepository extends BaseRepository<BatchPlanFieldLine, BatchPlanFieldLineDTO>, ProxySelf<BatchPlanFieldLineRepository> {

}
