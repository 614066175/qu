package org.xdsp.quality.infra.repository.impl;

import org.springframework.stereotype.Component;
import org.xdsp.core.base.repository.impl.BaseRepositoryImpl;
import org.xdsp.quality.api.dto.BatchPlanFieldConDTO;
import org.xdsp.quality.domain.entity.BatchPlanFieldCon;
import org.xdsp.quality.domain.repository.BatchPlanFieldConRepository;
import org.xdsp.quality.infra.dataobject.BatchPlanFieldConDO;
import org.xdsp.quality.infra.mapper.BatchPlanFieldConMapper;

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

    @Override
    public int deleteByPlanBaseId(Long planBaseId) {
        return batchPlanFieldConMapper.deleteByPlanBaseId(planBaseId);
    }

    @Override
    public int deleteByPlanLineId(Long planLineId) {
        return batchPlanFieldConMapper.deleteByPlanLineId(planLineId);
    }
}
