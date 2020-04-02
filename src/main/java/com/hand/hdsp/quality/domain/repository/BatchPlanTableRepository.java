package com.hand.hdsp.quality.domain.repository;

import com.hand.hdsp.core.base.ProxySelf;
import com.hand.hdsp.core.base.repository.BaseRepository;
import com.hand.hdsp.quality.api.dto.BatchPlanTableDTO;
import com.hand.hdsp.quality.domain.entity.BatchPlanTable;
import com.hand.hdsp.quality.infra.dataobject.BatchPlanTableDO;

import java.util.List;

/**
 * <p>批数据方案-表级规则表资源库</p>
 *
 * @author feng.liu01@hand-china.com 2020-03-24 16:19:52
 */
public interface BatchPlanTableRepository extends BaseRepository<BatchPlanTable, BatchPlanTableDTO>, ProxySelf<BatchPlanTableRepository> {

    /**
     * 表级规则
     *
     * @param batchPlanTableDO
     * @return
     */
    List<BatchPlanTableDO> list(BatchPlanTableDO batchPlanTableDO);

    /**
     * 删除
     *
     * @param planBaseId
     * @return
     */
    int deleteByParentId(Long planBaseId);
}
