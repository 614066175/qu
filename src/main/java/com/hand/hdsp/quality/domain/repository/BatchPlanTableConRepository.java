package com.hand.hdsp.quality.domain.repository;

import com.hand.hdsp.core.base.ProxySelf;
import com.hand.hdsp.core.base.repository.BaseRepository;
import com.hand.hdsp.quality.api.dto.BatchPlanTableConDTO;
import com.hand.hdsp.quality.domain.entity.BatchPlanTableCon;

/**
 * <p>批数据方案-表级规则条件表资源库</p>
 *
 * @author feng.liu01@hand-china.com 2020-06-03 20:03:41
 */
public interface BatchPlanTableConRepository extends BaseRepository<BatchPlanTableCon, BatchPlanTableConDTO>, ProxySelf<BatchPlanTableConRepository> {

}
