package com.hand.hdsp.quality.domain.repository;

import java.util.List;

import com.hand.hdsp.core.base.ProxySelf;
import com.hand.hdsp.core.base.repository.BaseRepository;
import com.hand.hdsp.quality.api.dto.StreamingPlanDTO;
import com.hand.hdsp.quality.api.dto.StreamingResultDTO;
import com.hand.hdsp.quality.domain.entity.StreamingPlan;

/**
 * <p>实时数据评估方案表资源库</p>
 *
 * @author feng.liu01@hand-china.com 2020-03-24 16:19:51
 */
public interface StreamingPlanRepository extends BaseRepository<StreamingPlan, StreamingPlanDTO>, ProxySelf<StreamingPlanRepository> {

    /**
     * 根据方案名查到所在分组
     *
     * @param streamingPlanDTO
     * @return
     */
    List<StreamingPlanDTO> getGroupByPlanName(StreamingPlanDTO streamingPlanDTO);
}
