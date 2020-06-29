package com.hand.hdsp.quality.domain.repository;

import com.hand.hdsp.core.base.ProxySelf;
import com.hand.hdsp.core.base.repository.BaseRepository;
import com.hand.hdsp.quality.api.dto.BatchPlanFieldDTO;
import com.hand.hdsp.quality.domain.entity.BatchPlanField;

import java.util.List;

/**
 * <p>批数据方案-字段规则表资源库</p>
 *
 * @author feng.liu01@hand-china.com 2020-03-24 16:19:53
 */
public interface BatchPlanFieldRepository extends BaseRepository<BatchPlanField, BatchPlanFieldDTO>, ProxySelf<BatchPlanFieldRepository> {

    /**
     * 删除
     *
     * @param planBaseId
     * @return
     */
    int deleteByPlanBaseId(Long planBaseId);

    /**
     * 关联查询
     *
     * @param batchPlanFieldDTO
     * @return
     */
    List<BatchPlanFieldDTO> selectList(BatchPlanFieldDTO batchPlanFieldDTO);

    /**
     * 列表
     *
     * @param batchPlanField
     * @return
     */
    List<BatchPlanField> list(BatchPlanField batchPlanField);

    /**
     * 字段规则-规则详情查询方法
     *
     * @param batchPlanFieldDTO
     * @return
     */
    List<BatchPlanFieldDTO> selectDetailList(BatchPlanFieldDTO batchPlanFieldDTO);
}
