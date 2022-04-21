package com.hand.hdsp.quality.app.service.impl;

import com.hand.hdsp.quality.api.dto.WorkOrderDTO;
import com.hand.hdsp.quality.api.dto.WorkOrderOperationDTO;
import com.hand.hdsp.quality.app.service.WorkOrderService;
import com.hand.hdsp.quality.domain.entity.WorkOrder;
import com.hand.hdsp.quality.domain.entity.WorkOrderOperation;
import com.hand.hdsp.quality.domain.repository.WorkOrderOperationRepository;
import com.hand.hdsp.quality.domain.repository.WorkOrderRepository;
import com.hand.hdsp.quality.infra.constant.ErrorCode;
import com.hand.hdsp.quality.infra.converter.WorkOrderConverter;
import com.hand.hdsp.quality.infra.mapper.WorkOrderMapper;
import io.choerodon.core.domain.Page;
import io.choerodon.core.exception.CommonException;
import io.choerodon.core.oauth.DetailsHelper;
import io.choerodon.mybatis.pagehelper.PageHelper;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import org.apache.commons.collections4.CollectionUtils;
import org.hzero.boot.message.MessageClient;
import org.hzero.boot.message.entity.Receiver;
import org.hzero.boot.platform.code.builder.CodeRuleBuilder;
import org.hzero.mybatis.domian.Condition;
import org.hzero.mybatis.util.Sqls;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

import static com.hand.hdsp.quality.infra.constant.WorkOrderConstants.*;

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
    private final WorkOrderConverter workOrderConverter;
    private final MessageClient messageClient;

    public static final String WORK_ORDER_CODE = "XQUA.WORK_ORDER_CODE";
    private static final String GLOBAL = "GLOBAL";

    public static final String ORDER_SUBMIT = "HDSP.XQUA.ORDER_SUBMIT";
    public static final String ORDER_REFUSE = "HDSP.XQUA.ORDER_REFUSE";

    public WorkOrderServiceImpl(WorkOrderRepository workOrderRepository, WorkOrderOperationRepository workOrderOperationRepository, CodeRuleBuilder codeRuleBuilder, WorkOrderMapper workOrderMapper, WorkOrderConverter workOrderConverter, MessageClient messageClient) {
        this.workOrderRepository = workOrderRepository;
        this.workOrderOperationRepository = workOrderOperationRepository;
        this.codeRuleBuilder = codeRuleBuilder;
        this.workOrderMapper = workOrderMapper;
        this.workOrderConverter = workOrderConverter;
        this.messageClient = messageClient;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<WorkOrderDTO> launchUpdate(List<WorkOrderDTO> workOrderDTOList) {
        //发起整改
        workOrderDTOList.forEach(workOrderDTO -> {
            //生成质量工单编码
            workOrderDTO.setWorkOrderCode(codeRuleBuilder.generateCode(DetailsHelper.getUserDetails().getTenantId(), WORK_ORDER_CODE, GLOBAL, GLOBAL, null));

            //默认为待接收状态
            workOrderDTO.setWorkOrderStatus(WorkOrderStatus.PENDING_RECEIVE);
        });
        List<WorkOrderDTO> workOrderDTOS = workOrderRepository.batchInsertDTOSelective(workOrderDTOList);

        //记录整改操作
        List<WorkOrderOperationDTO> workOrderOperationDTOList = new ArrayList<>();
        workOrderDTOS.forEach(workOrderDTO -> {
            WorkOrderOperationDTO workOrderOperationDTO = WorkOrderOperationDTO.builder()
                    .workOrderId(workOrderDTO.getWorkOrderId())
                    //创建人即为发起操作人
                    .operatorId(DetailsHelper.getUserDetails().getUserId())
                    .operateType(OrderOperateType.LAUNCH)
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

    public List<WorkOrderDTO> receiveOrRefuse(List<WorkOrderDTO> workOrderDTOList, String workOrderStatus, String orderOperateType) {
        //批量修改工单状态
        Optional<WorkOrderDTO> any = workOrderDTOList.stream()
                .filter(workOrderDTO -> !WorkOrderStatus.RECEIVED.equals(workOrderDTO.getWorkOrderStatus()))
                .findAny();
        if (any.isPresent()) {
            throw new CommonException(ErrorCode.WORK_ORDER_STATUS_ERROR);
        }

        List<WorkOrderOperationDTO> workOrderOperationDTOList = new ArrayList<>();
        List<WorkOrder> workOrderList = new ArrayList<>();
        WorkOrderOperationDTO workOrderOperationDTO;
        WorkOrder workOrder;
        for (WorkOrderDTO workOrderDTO : workOrderDTOList) {
            //修改工单状态
            workOrder = workOrderConverter.dtoToEntity(workOrderDTO);
            workOrder.setWorkOrderStatus(workOrderStatus);
            workOrderList.add(workOrder);

            //记录操作日志
            workOrderOperationDTO = WorkOrderOperationDTO.builder()
                    .workOrderId(workOrderDTO.getWorkOrderId())
                    .operatorId(DetailsHelper.getUserDetails().getUserId())
                    .operateType(orderOperateType)
                    .processComment(workOrderDTO.getProcessComment())
                    .build();
            workOrderOperationDTOList.add(workOrderOperationDTO);
        }
        //批量更改工单状态
        workOrderRepository.batchUpdateOptional(workOrderList, WorkOrder.FIELD_WORK_ORDER_STATUS);

        //批量记录工单操作
        workOrderOperationRepository.batchInsertDTOSelective(workOrderOperationDTOList);
        //如果是拒绝，则发送消息通知
        if (OrderOperateType.REFUSE.equals(orderOperateType)) {
            Receiver receiver;
            for (WorkOrder order : workOrderList) {
                //接受组为发起人
                receiver = new Receiver();
                receiver.setUserId(order.getCreatedBy());
                receiver.setTargetUserTenantId(order.getTenantId());
                //模板内容 您发起的${工单号}工单已被拒绝，可确认后重新提交。
                Map<String, String> args = new HashMap<>();
                args.put("workOrderCode", order.getWorkOrderCode());
                //发送站内信
                messageClient.sendWebMessage(order.getTenantId(), ORDER_REFUSE, null, Collections.singletonList(receiver), args);
            }
        }
        return workOrderDTOList;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<WorkOrderDTO> batchReceive(List<WorkOrderDTO> workOrderDTOList) {
        //接收
        return receiveOrRefuse(workOrderDTOList, WorkOrderStatus.RECEIVED, OrderOperateType.RECEIVE);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<WorkOrderDTO> batchRefuse(List<WorkOrderDTO> workOrderDTOList) {
        //拒绝
        return receiveOrRefuse(workOrderDTOList, WorkOrderStatus.REFUSED, OrderOperateType.REFUSE);
    }

    @Override
    public List<WorkOrderOperationDTO> oderOperateInfo(Long workOrderId) {
        return workOrderMapper.oderOperateInfo(workOrderId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<Long> revokeOrder(List<Long> workOrderIdList) {
        if (CollectionUtils.isEmpty(workOrderIdList)) {
            return null;
        }
        //	如果工单的状态还未开始处理，可以撤回整改
        List<WorkOrder> workOrderList = new ArrayList<>();
        WorkOrder workOrder;
        List<WorkOrderOperation> allWorkOrderOperationList = new ArrayList<>();
        List<WorkOrderOperation> workOrderOperations;
        for (Long workOrderId : workOrderIdList) {
            workOrder = workOrderRepository.selectByPrimaryKey(workOrderId);
            if (workOrder == null) {
                continue;
            }
            //如果处理中或者已处理，则无法进行撤回
            if (WorkOrderStatus.PROCESSING.equals(workOrder.getWorkOrderStatus())
                    || WorkOrderStatus.PROCESSED.equals(workOrder.getWorkOrderStatus())) {
                throw new CommonException(ErrorCode.WORK_ORDER_STATUS_CAN_NOT_REVOKE);
            }
            workOrderList.add(workOrder);
            workOrderOperations = workOrderOperationRepository.selectByCondition(Condition.builder(WorkOrderOperation.class)
                    .andWhere(Sqls.custom().andEqualTo(WorkOrderOperation.FIELD_WORK_ORDER_ID, workOrderId))
                    .build());
            allWorkOrderOperationList.addAll(workOrderOperations);
        }
        //批量删除
        workOrderRepository.batchDeleteByPrimaryKey(workOrderList);
        //批量删除的所有相关的工单操作日志
        workOrderOperationRepository.batchDeleteByPrimaryKey(allWorkOrderOperationList);
        return workOrderIdList;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public WorkOrderDTO startProcess(Long workOrderId) {
        //开始处理质量工单
        WorkOrderDTO workOrderDTO = workOrderRepository.selectDTOByPrimaryKey(workOrderId);
        if (workOrderDTO == null) {
            throw new CommonException(ErrorCode.WORK_ORDER_NOT_EXIST);
        }
        //设置为待处理
        workOrderDTO.setWorkOrderStatus(WorkOrderStatus.PENDING_PROCESS);
        //操作记录为开始处理
        WorkOrderOperationDTO workOrderOperationDTO = WorkOrderOperationDTO.builder()
                .workOrderId(workOrderId)
                .operatorId(DetailsHelper.getUserDetails().getUserId())
                .operateType(OrderOperateType.START_PROCESS)
                .build();
        workOrderRepository.updateDTOOptional(workOrderDTO, WorkOrder.FIELD_WORK_ORDER_STATUS);
        workOrderOperationRepository.insertDTOSelective(workOrderOperationDTO);
        return workOrderDTO;
    }

    @Override
    public WorkOrderDTO orderAssign(WorkOrderDTO workOrderDTO) {
        //分派质量工单
        //分派不调整工单状态，更改处理人
        workOrderDTO.setProcessorsId(workOrderDTO.getAssignId());

        //操作记录为开始处理
        WorkOrderOperationDTO workOrderOperationDTO = WorkOrderOperationDTO.builder()
                .workOrderId(workOrderDTO.getWorkOrderId())
                .operatorId(DetailsHelper.getUserDetails().getUserId())
                .operateType(OrderOperateType.ASSIGN)
                .processComment(String.format("%s分派给%s", DetailsHelper.getUserDetails().getRealName(), workOrderDTO.getAssignName()))
                .build();
        workOrderRepository.updateDTOOptional(workOrderDTO, WorkOrder.FIELD_PROCESSORS_ID);
        workOrderOperationRepository.insertDTOSelective(workOrderOperationDTO);
        return workOrderDTO;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public WorkOrderDTO orderSubmit(WorkOrderDTO workOrderDTO) {
        //修改状态为已处理
        workOrderDTO.setWorkOrderStatus(WorkOrderStatus.PROCESSED);

        //记录提交解决方案的操作日志
        WorkOrderOperationDTO workOrderOperationDTO = WorkOrderOperationDTO.builder()
                .workOrderId(workOrderDTO.getWorkOrderId())
                .operatorId(DetailsHelper.getUserDetails().getUserId())
                .operateType(OrderOperateType.SUBMIT_SOLUTION)
                .build();


        workOrderRepository.updateDTOOptional(workOrderDTO, WorkOrder.FIELD_WORK_ORDER_STATUS);
        workOrderOperationRepository.insertDTOSelective(workOrderOperationDTO);

        //接受组为发起人
        Receiver receiver = new Receiver();
        receiver.setUserId(workOrderDTO.getCreatedBy());
        receiver.setTargetUserTenantId(workOrderDTO.getTenantId());

        //模板内容 为您发起的${workOrderCode}工单已解决，请确认。
        Map<String, String> args = new HashMap<>();
        args.put("workOrderCode", workOrderDTO.getWorkOrderCode());
        //发送站内信
        messageClient.sendWebMessage(workOrderDTO.getTenantId(), ORDER_SUBMIT, null, Collections.singletonList(receiver), args);
        return workOrderDTO;
    }

}
