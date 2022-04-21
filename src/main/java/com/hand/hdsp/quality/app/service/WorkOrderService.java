package com.hand.hdsp.quality.app.service;

import com.hand.hdsp.quality.api.dto.WorkOrderDTO;
import com.hand.hdsp.quality.api.dto.WorkOrderOperationDTO;
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


    /**
     * 批量接收工单
     * @param workOrderDTOList
     * @return
     */
    List<WorkOrderDTO> batchReceive(List<WorkOrderDTO> workOrderDTOList);

    /**
     * 批量拒绝工单
     * @param workOrderDTOList
     * @return
     */
    List<WorkOrderDTO> batchRefuse(List<WorkOrderDTO> workOrderDTOList);

    /**
     * 工单操作流程信息
     * @param workOrderId
     * @return
     */
    List<WorkOrderOperationDTO> oderOperateInfo(Long workOrderId);

    /**
     * 撤回质量整改工单
     * @param workOrderIdList
     * @return
     */
    List<Long> revokeOrder(List<Long> workOrderIdList);

    /**
     * 开始处理质量工单
     * @param workOrderId
     * @return
     */
    WorkOrderDTO startProcess(Long workOrderId);

    /**
     * 质量工单分派
     * @param workOrderDTO
     * @return
     */
    WorkOrderDTO orderAssign(WorkOrderDTO workOrderDTO);

    /**
     * 质量工单提交
     * @param workOrderDTO
     * @return
     */
    WorkOrderDTO orderSubmit(WorkOrderDTO workOrderDTO);
}
