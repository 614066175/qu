package com.hand.hdsp.quality.domain.repository;

import com.hand.hdsp.core.base.ProxySelf;
import com.hand.hdsp.core.base.repository.BaseRepository;
import com.hand.hdsp.quality.api.dto.BatchPlanTableDTO;
import com.hand.hdsp.quality.domain.entity.BatchPlanTable;

/**
 * <p>批数据方案-表级规则表资源库</p>
 *
 * @author feng.liu01@hand-china.com 2020-03-24 16:19:52
 */
public interface BatchPlanTableRepository extends BaseRepository<BatchPlanTable, BatchPlanTableDTO>, ProxySelf<BatchPlanTableRepository> {

}
