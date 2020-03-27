package com.hand.hdsp.quality.infra.mapper;

import java.util.List;

import com.hand.hdsp.quality.api.dto.BatchPlanDTO;
import com.hand.hdsp.quality.domain.entity.BatchPlan;
import io.choerodon.mybatis.common.BaseMapper;

/**
 * <p>批数据评估方案表Mapper</p>
 *
 * @author feng.liu01@hand-china.com 2020-03-24 16:19:52
 */
public interface BatchPlanMapper extends BaseMapper<BatchPlan> {

    /**
     * 根据方案名查到所在分组
     *
     * @param batchPlanDTO
     * @return BatchPlanDTO
     */
    List<BatchPlanDTO> getGroupByPlanName(BatchPlanDTO batchPlanDTO);
}
