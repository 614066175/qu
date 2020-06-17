package com.hand.hdsp.quality.domain.repository;

import com.hand.hdsp.core.base.ProxySelf;
import com.hand.hdsp.core.base.repository.BaseRepository;
import com.hand.hdsp.quality.api.dto.BatchPlanBaseDTO;
import com.hand.hdsp.quality.domain.entity.BatchPlanBase;
import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;

/**
 * <p>批数据方案-基础配置表资源库</p>
 *
 * @author feng.liu01@hand-china.com 2020-03-24 16:19:53
 */
public interface BatchPlanBaseRepository extends BaseRepository<BatchPlanBase, BatchPlanBaseDTO>, ProxySelf<BatchPlanBaseRepository> {

    /**
     * 行列表
     *
     * @param pageRequest      分页条件
     * @param batchPlanBaseDTO 查询条件
     * @return Page<BatchPlanBaseDTO> 实体类page
     */
    Page<BatchPlanBaseDTO> list(PageRequest pageRequest, BatchPlanBaseDTO batchPlanBaseDTO);

    /**
     * 查询明细
     *
     * @param planBaseId 主键
     * @return BatchPlanBaseDTO 实体类
     */
    BatchPlanBaseDTO detail(Long planBaseId);
}
