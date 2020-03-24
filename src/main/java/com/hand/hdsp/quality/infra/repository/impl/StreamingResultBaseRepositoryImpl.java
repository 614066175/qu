package com.hand.hdsp.quality.infra.repository.impl;

import com.hand.hdsp.core.base.repository.impl.BaseRepositoryImpl;
import com.hand.hdsp.quality.api.dto.StreamingResultBaseDTO;
import com.hand.hdsp.quality.domain.entity.StreamingResultBase;
import com.hand.hdsp.quality.domain.repository.StreamingResultBaseRepository;
import org.springframework.stereotype.Component;

/**
 * <p>实时数据方案结果表-基础信息资源库实现</p>
 *
 * @author feng.liu01@hand-china.com 2020-03-24 16:19:50
 */
@Component
public class StreamingResultBaseRepositoryImpl extends BaseRepositoryImpl<StreamingResultBase, StreamingResultBaseDTO> implements StreamingResultBaseRepository {

}
