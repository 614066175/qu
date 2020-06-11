package com.hand.hdsp.quality.infra.repository.impl;

import com.hand.hdsp.core.base.repository.impl.BaseRepositoryImpl;
import com.hand.hdsp.quality.api.dto.BatchResultItemDTO;
import com.hand.hdsp.quality.domain.entity.BatchResultItem;
import com.hand.hdsp.quality.domain.repository.BatchResultItemRepository;
import org.springframework.stereotype.Component;

/**
 * <p>批数据方案结果表-校验项信息资源库实现</p>
 *
 * @author feng.liu01@hand-china.com 2020-06-03 20:28:29
 */
@Component
public class BatchResultItemRepositoryImpl extends BaseRepositoryImpl<BatchResultItem, BatchResultItemDTO> implements BatchResultItemRepository {

}
