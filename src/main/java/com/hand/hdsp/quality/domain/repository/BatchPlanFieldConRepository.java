package com.hand.hdsp.quality.domain.repository;

import com.hand.hdsp.core.base.ProxySelf;
import com.hand.hdsp.core.base.repository.BaseRepository;
import com.hand.hdsp.quality.api.dto.BatchPlanFieldConDTO;
import com.hand.hdsp.quality.domain.entity.BatchPlanFieldCon;

/**
 * <p>批数据方案-字段规则条件表资源库</p>
 *
 * @author feng.liu01@hand-china.com 2020-06-03 20:03:41
 */
public interface BatchPlanFieldConRepository extends BaseRepository<BatchPlanFieldCon, BatchPlanFieldConDTO>, ProxySelf<BatchPlanFieldConRepository> {

}
