package com.hand.hdsp.quality.infra.mapper;

import java.util.List;

import com.hand.hdsp.quality.api.dto.StreamingPlanDTO;
import com.hand.hdsp.quality.domain.entity.StreamingPlan;
import io.choerodon.mybatis.common.BaseMapper;

/**
 * <p>实时数据评估方案表Mapper</p>
 *
 * @author feng.liu01@hand-china.com 2020-03-24 16:19:51
 */
public interface StreamingPlanMapper extends BaseMapper<StreamingPlan> {

    /**
     * 根据方案名查到所在分组
     *
     * @param streamingPlanDTO
     * @return
     */
    List<StreamingPlanDTO> getGroupByPlanName(StreamingPlanDTO streamingPlanDTO);
}
