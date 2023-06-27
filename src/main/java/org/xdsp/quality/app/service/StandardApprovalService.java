package org.xdsp.quality.app.service;


import org.xdsp.quality.api.dto.StandardApprovalDTO;

/**
 * <p>各种标准审批表应用服务</p>
 *
 * @author fuqiang.luo@hand-china.com 2022-08-31 10:30:34
 */
public interface StandardApprovalService {

    /**
     * 创建或者更新标准审批
     * @param standardApprovalDTO   记录
     * @return                      记录
     */
    StandardApprovalDTO createOrUpdate(StandardApprovalDTO standardApprovalDTO);

    /**
     * 删除记录
     * @param standardApprovalDTO 记录
     */
    void delete(StandardApprovalDTO standardApprovalDTO);

}
