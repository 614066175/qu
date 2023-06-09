package org.xdsp.quality.infra.mapper;


import io.choerodon.mybatis.common.BaseMapper;
import org.xdsp.quality.api.dto.BatchPlanDTO;
import org.xdsp.quality.domain.entity.BatchPlan;

import java.util.List;

/**
 * <p>批数据评估方案表Mapper</p>
 *
 * @author feng.liu01@hand-china.com 2020-03-24 16:19:52
 */
public interface BatchPlanMapper extends BaseMapper<BatchPlan> {

    /**
     * 根据项目id获取项目编码
     * @param projectId
     */
    String getProjectCodeById(Long projectId);

    /**
     * 查询分组评估方案
     * @param groupIds
     * @param tenantId
     * @param projectId
     * @return
     */
    List<BatchPlanDTO> selectGroupPlans(List<Long> groupIds, Long tenantId, Long projectId);
}
