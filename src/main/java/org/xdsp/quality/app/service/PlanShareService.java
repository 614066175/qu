package org.xdsp.quality.app.service;

import org.xdsp.quality.api.dto.PlanShareDTO;

import java.util.List;

/**
 * <p>应用服务</p>
 *
 * @author guoliang.li01@hand-china.com 2022-09-26 14:04:11
 */
public interface PlanShareService {

    /**
     * 查询方案共享的项目
     * @param planId
     * @return
     */
    List<PlanShareDTO> shareProjects(Long planId);

    /**
     * 方案共享
     * @param tenantId
     * @param projectId
     * @param planId
     * @param planShareDTOList
     * @return
     */
    List<PlanShareDTO> batchShare(Long tenantId, Long projectId, Long planId, List<PlanShareDTO> planShareDTOList);
}
