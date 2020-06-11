package com.hand.hdsp.quality.domain.repository;

import com.hand.hdsp.core.base.ProxySelf;
import com.hand.hdsp.core.base.repository.BaseRepository;
import com.hand.hdsp.quality.api.dto.BatchResultItemDTO;
import com.hand.hdsp.quality.domain.entity.BatchResultItem;

/**
 * <p>批数据方案结果表-校验项信息资源库</p>
 *
 * @author feng.liu01@hand-china.com 2020-06-03 20:28:29
 */
public interface BatchResultItemRepository extends BaseRepository<BatchResultItem, BatchResultItemDTO>, ProxySelf<BatchResultItemRepository> {

}
