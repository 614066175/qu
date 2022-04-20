package com.hand.hdsp.quality.app.service;

import com.hand.hdsp.quality.api.dto.WorkOrderDTO;
import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;

import java.util.List;

/**
 * <p>应用服务</p>
 *
 * @author guoliang.li01@hand-china.com 2022-04-20 16:28:13
 */
public interface WorkOrderService {

    /**
     * 发起整改
     * @param workOrderDTOList
     * @return
     */
    List<WorkOrderDTO> launchUpdate(List<WorkOrderDTO> workOrderDTOList);

    /**
     * 我的待处理工单列表
     * @param workOrderDTO
     * @param pageRequest
     * @return
     */
    Page<WorkOrderDTO> orderTodo(WorkOrderDTO workOrderDTO, PageRequest pageRequest);

    /**
     * 我发起的工单列表
     * @param workOrderDTO
     * @param pageRequest
     * @return
     */
    Page<WorkOrderDTO> orderApply(WorkOrderDTO workOrderDTO, PageRequest pageRequest);
}
