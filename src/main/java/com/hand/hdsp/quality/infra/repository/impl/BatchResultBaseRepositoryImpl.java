package com.hand.hdsp.quality.infra.repository.impl;

import com.hand.hdsp.core.base.repository.impl.BaseRepositoryImpl;
import com.hand.hdsp.quality.api.dto.BatchResultBaseDTO;
import com.hand.hdsp.quality.domain.entity.BatchResultBase;
import com.hand.hdsp.quality.domain.repository.BatchResultBaseRepository;
import org.springframework.stereotype.Component;

/**
 * <p>批数据方案结果表-表信息资源库实现</p>
 *
 * @author feng.liu01@hand-china.com 2020-03-24 16:19:52
 */
@Component
public class BatchResultBaseRepositoryImpl extends BaseRepositoryImpl<BatchResultBase, BatchResultBaseDTO> implements BatchResultBaseRepository {

}
