package com.hand.hdsp.quality.domain.repository;

import com.hand.hdsp.core.base.ProxySelf;
import com.hand.hdsp.core.base.repository.BaseRepository;
import com.hand.hdsp.quality.api.dto.BatchPlanRelTableLineDTO;
import com.hand.hdsp.quality.domain.entity.BatchPlanRelTableLine;

/**
 * <p>批数据方案-表间规则关联关系表资源库</p>
 *
 * @author feng.liu01@hand-china.com 2020-03-24 16:19:53
 */
public interface BatchPlanRelTableLineRepository extends BaseRepository<BatchPlanRelTableLine, BatchPlanRelTableLineDTO>, ProxySelf<BatchPlanRelTableLineRepository> {

}
