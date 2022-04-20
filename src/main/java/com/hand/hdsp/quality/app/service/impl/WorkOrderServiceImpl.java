package com.hand.hdsp.quality.app.service.impl;

import com.hand.hdsp.quality.api.dto.WorkOrderDTO;
import com.hand.hdsp.quality.api.dto.WorkOrderOperationDTO;
import com.hand.hdsp.quality.app.service.WorkOrderService;
import com.hand.hdsp.quality.domain.repository.WorkOrderOperationRepository;
import com.hand.hdsp.quality.domain.repository.WorkOrderRepository;
import com.hand.hdsp.quality.infra.constant.WorkOrderConstants;
import com.hand.hdsp.quality.infra.mapper.WorkOrderMapper;
import io.choerodon.core.domain.Page;
import io.choerodon.core.oauth.DetailsHelper;
import io.choerodon.mybatis.pagehelper.PageHelper;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import org.hzero.boot.platform.code.builder.CodeRuleBuilder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>应用服务默认实现</p>
 *
 * @author guoliang.li01@hand-china.com 2022-04-20 16:28:13
 */
@Service
public class WorkOrderServiceImpl implements WorkOrderService {
    private final WorkOrderRepository workOrderRepository;
    private final WorkOrderOperationRepository workOrderOperationRepository;
    private final CodeRuleBuilder codeRuleBuilder;
    private final WorkOrderMapper workOrderMapper;

    public static final String WORK_ORDER_CODE = "XQUA.WORK_ORDER_CODE";
    private static final String GLOBAL = "GLOBAL";

    public WorkOrderServiceImpl(WorkOrderRepository workOrderRepository, WorkOrderOperationRepository workOrderOperationRepository, CodeRuleBuilder codeRuleBuilder, WorkOrderMapper workOrderMapper) {
        this.workOrderRepository = workOrderRepository;
        this.workOrderOperationRepository = workOrderOperationRepository;
        this.codeRuleBuilder = codeRuleBuilder;
        this.workOrderMapper = workOrderMapper;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<WorkOrderDTO> launchUpdate(List<WorkOrderDTO> workOrderDTOList) {
        //发起整改
        workOrderDTOList.forEach(workOrderDTO -> {
            //生成质量工单编码
            workOrderDTO.setWorkOrderCode(codeRuleBuilder.generateCode(DetailsHelper.getUserDetails().getTenantId(), WORK_ORDER_CODE, GLOBAL, GLOBAL, null));

            //默认为待接收状态
            workOrderDTO.setWorkOrderStatus(WorkOrderConstants.WorkOrderStatus.PENDING_RECEIVE);
        });
        List<WorkOrderDTO> workOrderDTOS = workOrderRepository.batchInsertDTOSelective(workOrderDTOList);

        //记录整改操作
        List<WorkOrderOperationDTO> workOrderOperationDTOList = new ArrayList<>();
        workOrderDTOS.forEach(workOrderDTO -> {
            WorkOrderOperationDTO workOrderOperationDTO = WorkOrderOperationDTO.builder()
                    .workOrderId(workOrderDTO.getWorkOrderId())
                    //创建人即为发起操作人
                    .operatorId(DetailsHelper.getUserDetails().getUserId())
                    .operateType(WorkOrderConstants.OrderOperateType.LAUNCH)
                    //发起时的操作描述即为发起整改时的工单备注
                    .processComment(workOrderDTO.getOrderDesc())
                    .build();
            workOrderOperationDTOList.add(workOrderOperationDTO);
        });
        workOrderOperationRepository.batchInsertDTOSelective(workOrderOperationDTOList);
        return workOrderDTOList;
    }

    @Override
    public Page<WorkOrderDTO> orderTodo(WorkOrderDTO workOrderDTO, PageRequest pageRequest) {
        return PageHelper.doPageAndSort(pageRequest, () -> workOrderMapper.orderTodo(workOrderDTO));
    }

    @Override
    public Page<WorkOrderDTO> orderApply(WorkOrderDTO workOrderDTO, PageRequest pageRequest) {
        return PageHelper.doPageAndSort(pageRequest, () -> workOrderMapper.orderApply(workOrderDTO));
    }

}
