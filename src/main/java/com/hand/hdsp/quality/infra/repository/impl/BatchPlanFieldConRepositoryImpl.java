package com.hand.hdsp.quality.infra.repository.impl;

import com.hand.hdsp.core.base.repository.impl.BaseRepositoryImpl;
import com.hand.hdsp.quality.api.dto.BatchPlanFieldConDTO;
import com.hand.hdsp.quality.domain.entity.BatchPlanFieldCon;
import com.hand.hdsp.quality.domain.repository.BatchPlanFieldConRepository;
import com.hand.hdsp.quality.infra.dataobject.BatchPlanFieldConDO;
import com.hand.hdsp.quality.infra.mapper.BatchPlanFieldConMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * <p>批数据方案-字段规则条件表资源库实现</p>
 *
 * @author feng.liu01@hand-china.com 2020-06-03 20:03:41
 */
@Component
public class BatchPlanFieldConRepositoryImpl extends BaseRepositoryImpl<BatchPlanFieldCon, BatchPlanFieldConDTO> implements BatchPlanFieldConRepository {

    private final BatchPlanFieldConMapper batchPlanFieldConMapper;

    public BatchPlanFieldConRepositoryImpl(BatchPlanFieldConMapper batchPlanFieldConMapper) {
        this.batchPlanFieldConMapper = batchPlanFieldConMapper;
    }

    @Override
    public List<BatchPlanFieldConDO> selectJoinItem(BatchPlanFieldConDO batchPlanFieldConDO) {
        return batchPlanFieldConMapper.selectJoinItem(batchPlanFieldConDO);
    }
}
