package com.hand.hdsp.quality.infra.mapper;

import com.hand.hdsp.quality.api.dto.WorkOrderDTO;
import com.hand.hdsp.quality.api.dto.WorkOrderOperationDTO;
import com.hand.hdsp.quality.domain.entity.WorkOrder;
import io.choerodon.mybatis.common.BaseMapper;

import java.util.List;

/**
 * <p>Mapper</p>
 *
 * @author guoliang.li01@hand-china.com 2022-04-20 16:28:13
 */
public interface WorkOrderMapper extends BaseMapper<WorkOrder> {

    /**
     * 我的代办工单
     * @param workOrderDTO
     * @return
     */
    List<WorkOrderDTO> orderTodo(WorkOrderDTO workOrderDTO);

    /**
     * 我发起的工单
     * @param workOrderDTO
     * @return
     */
    List<WorkOrderDTO> orderApply(WorkOrderDTO workOrderDTO);

    /**
     * 查询工单操作流程信息
     * @param workOrderId
     * @return
     */
    List<WorkOrderOperationDTO> oderOperateInfo(Long workOrderId);
}
