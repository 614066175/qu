package org.xdsp.quality.infra.mapper;

import io.choerodon.mybatis.common.BaseMapper;
import org.xdsp.quality.api.dto.WorkOrderDTO;
import org.xdsp.quality.api.dto.WorkOrderOperationDTO;
import org.xdsp.quality.domain.entity.WorkOrder;

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


    /**
     * 工单详情
     * @param workOrderId
     * @return
     */
    WorkOrderDTO detail(Long workOrderId);

    /**
     * 根据id查找用户未解密邮箱
     * @param processorsId 处理人用户id
     * @return  未解密邮箱
     */
    String findUserEmail(Long processorsId);

    /**
     * 根据id查找用户未解密的电话
     * @param processorsId  处理人用户id
     * @return  未解密的电话
     */
    String findUserPhone(Long processorsId);
}
