package org.xdsp.quality.domain.repository;

import org.xdsp.core.base.ProxySelf;
import org.xdsp.core.base.repository.BaseRepository;
import org.xdsp.quality.api.dto.StreamingPlanDTO;
import org.xdsp.quality.domain.entity.PlanGroup;
import org.xdsp.quality.domain.entity.StreamingPlan;

import java.util.List;

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
     * @return StreamingPlanDTO
     */
    List<PlanGroup> getGroupByPlanName(StreamingPlanDTO streamingPlanDTO);

}
