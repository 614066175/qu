package com.hand.hdsp.quality.infra.repository.impl;

import com.hand.hdsp.core.base.repository.impl.BaseRepositoryImpl;
import com.hand.hdsp.quality.api.dto.BatchPlanFieldConDTO;
import com.hand.hdsp.quality.domain.entity.BatchPlanFieldCon;
import com.hand.hdsp.quality.domain.repository.BatchPlanFieldConRepository;
import org.springframework.stereotype.Component;

/**
 * <p>批数据方案-字段规则条件表资源库实现</p>
 *
 * @author feng.liu01@hand-china.com 2020-06-03 20:03:41
 */
@Component
public class BatchPlanFieldConRepositoryImpl extends BaseRepositoryImpl<BatchPlanFieldCon, BatchPlanFieldConDTO> implements BatchPlanFieldConRepository {

}
