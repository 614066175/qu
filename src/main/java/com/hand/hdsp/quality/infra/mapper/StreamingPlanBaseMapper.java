package com.hand.hdsp.quality.infra.mapper;

import com.hand.hdsp.quality.api.dto.StreamingPlanBaseDTO;
import com.hand.hdsp.quality.domain.entity.StreamingPlanBase;
import io.choerodon.mybatis.common.BaseMapper;

import java.util.List;

/**
 * <p>实时数据方案-基础配置表Mapper</p>
 *
 * @author feng.liu01@hand-china.com 2020-03-24 16:19:51
 */
public interface StreamingPlanBaseMapper extends BaseMapper<StreamingPlanBase> {

    /**
     * 行列表
     *
     * @param streamingPlanBaseDTO 查询条件
     * @return
     */
    List<StreamingPlanBaseDTO> list(StreamingPlanBaseDTO streamingPlanBaseDTO);
}
