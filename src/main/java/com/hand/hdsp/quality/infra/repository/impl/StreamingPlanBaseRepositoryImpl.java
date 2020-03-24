package com.hand.hdsp.quality.infra.repository.impl;

import com.hand.hdsp.core.base.repository.impl.BaseRepositoryImpl;
import com.hand.hdsp.quality.api.dto.StreamingPlanBaseDTO;
import com.hand.hdsp.quality.domain.entity.StreamingPlanBase;
import com.hand.hdsp.quality.domain.repository.StreamingPlanBaseRepository;
import org.springframework.stereotype.Component;

/**
 * <p>实时数据方案-基础配置表资源库实现</p>
 *
 * @author feng.liu01@hand-china.com 2020-03-24 16:19:51
 */
@Component
public class StreamingPlanBaseRepositoryImpl extends BaseRepositoryImpl<StreamingPlanBase, StreamingPlanBaseDTO> implements StreamingPlanBaseRepository {

}
