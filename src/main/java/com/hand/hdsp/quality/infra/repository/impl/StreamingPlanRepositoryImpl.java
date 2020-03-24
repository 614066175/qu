package com.hand.hdsp.quality.infra.repository.impl;

import com.hand.hdsp.core.base.repository.impl.BaseRepositoryImpl;
import com.hand.hdsp.quality.api.dto.StreamingPlanDTO;
import com.hand.hdsp.quality.domain.entity.StreamingPlan;
import com.hand.hdsp.quality.domain.repository.StreamingPlanRepository;
import org.springframework.stereotype.Component;

/**
 * <p>实时数据评估方案表资源库实现</p>
 *
 * @author feng.liu01@hand-china.com 2020-03-24 16:19:51
 */
@Component
public class StreamingPlanRepositoryImpl extends BaseRepositoryImpl<StreamingPlan, StreamingPlanDTO> implements StreamingPlanRepository {

}
