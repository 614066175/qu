package com.hand.hdsp.quality.infra.repository.impl;

import com.hand.hdsp.core.base.repository.impl.BaseRepositoryImpl;
import com.hand.hdsp.quality.api.dto.BatchPlanBaseDTO;
import com.hand.hdsp.quality.domain.entity.BatchPlanBase;
import com.hand.hdsp.quality.domain.repository.BatchPlanBaseRepository;
import com.hand.hdsp.quality.infra.mapper.BatchPlanBaseMapper;
import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.PageHelper;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import org.springframework.stereotype.Component;

/**
 * <p>批数据方案-基础配置表资源库实现</p>
 *
 * @author feng.liu01@hand-china.com 2020-03-24 16:19:53
 */
@Component
public class BatchPlanBaseRepositoryImpl extends BaseRepositoryImpl<BatchPlanBase, BatchPlanBaseDTO> implements BatchPlanBaseRepository {

    private BatchPlanBaseMapper batchPlanBaseMapper;

    public BatchPlanBaseRepositoryImpl(BatchPlanBaseMapper batchPlanBaseMapper) {
        this.batchPlanBaseMapper = batchPlanBaseMapper;
    }

    @Override
    public Page<BatchPlanBaseDTO> list(PageRequest pageRequest, BatchPlanBaseDTO batchPlanBaseDTO) {
        return PageHelper.doPage(pageRequest, () -> batchPlanBaseMapper.list(batchPlanBaseDTO));
    }
}
