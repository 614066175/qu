package com.hand.hdsp.quality.infra.repository.impl;

import com.hand.hdsp.core.base.repository.impl.BaseRepositoryImpl;
import com.hand.hdsp.quality.api.dto.BatchPlanFieldDTO;
import com.hand.hdsp.quality.domain.entity.BatchPlanField;
import com.hand.hdsp.quality.domain.repository.BatchPlanFieldRepository;
import org.springframework.stereotype.Component;

/**
 * <p>批数据方案-字段规则表资源库实现</p>
 *
 * @author feng.liu01@hand-china.com 2020-03-24 16:19:53
 */
@Component
public class BatchPlanFieldRepositoryImpl extends BaseRepositoryImpl<BatchPlanField, BatchPlanFieldDTO> implements BatchPlanFieldRepository {

}
