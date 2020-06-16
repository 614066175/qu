package com.hand.hdsp.quality.infra.repository.impl;

import com.hand.hdsp.core.base.repository.impl.BaseRepositoryImpl;
import com.hand.hdsp.quality.api.dto.BatchResultItemDTO;
import com.hand.hdsp.quality.api.dto.BatchResultRuleDTO;
import com.hand.hdsp.quality.domain.entity.BatchResultItem;
import com.hand.hdsp.quality.domain.repository.BatchResultItemRepository;
import com.hand.hdsp.quality.infra.mapper.BatchResultItemMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * <p>批数据方案结果表-校验项信息资源库实现</p>
 *
 * @author feng.liu01@hand-china.com 2020-06-03 20:28:29
 */
@Component
public class BatchResultItemRepositoryImpl extends BaseRepositoryImpl<BatchResultItem, BatchResultItemDTO> implements BatchResultItemRepository {

    @Autowired
    private BatchResultItemMapper batchResultItemMapper;

    @Override
    public List<BatchResultItemDTO> selectByResultId(Long resultId) {
        return batchResultItemMapper.selectByResultId(resultId);
    }


    @Override
    public List<String> selectWaringLevelByResultId(Long resultId) {
        return batchResultItemMapper.selectWaringLevelByResultId(resultId);
    }
}
