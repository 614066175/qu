package org.xdsp.quality.infra.mapper;

import io.choerodon.mybatis.common.BaseMapper;
import org.xdsp.quality.api.dto.StreamingPlanBaseDTO;
import org.xdsp.quality.domain.entity.StreamingPlanBase;

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
