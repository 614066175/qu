package org.xdsp.quality.app.service.impl;

import com.google.common.base.Strings;
import io.choerodon.core.domain.Page;
import io.choerodon.core.exception.CommonException;
import io.choerodon.core.oauth.CustomUserDetails;
import io.choerodon.core.oauth.DetailsHelper;
import io.choerodon.mybatis.pagehelper.PageHelper;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.hzero.boot.message.MessageClient;
import org.hzero.boot.message.entity.*;
import org.hzero.boot.platform.code.builder.CodeRuleBuilder;
import org.hzero.core.base.BaseConstants;
import org.hzero.mybatis.domian.Condition;
import org.hzero.mybatis.util.Sqls;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.xdsp.core.profile.XdspProfileClient;
import org.xdsp.core.util.FlyBookHelper;
import org.xdsp.quality.api.dto.WorkOrderDTO;
import org.xdsp.quality.api.dto.WorkOrderOperationDTO;
import org.xdsp.quality.app.service.WorkOrderService;
import org.xdsp.quality.domain.entity.WorkOrder;
import org.xdsp.quality.domain.entity.WorkOrderOperation;
import org.xdsp.quality.domain.repository.WorkOrderOperationRepository;
import org.xdsp.quality.domain.repository.WorkOrderRepository;
import org.xdsp.quality.infra.constant.ErrorCode;
import org.xdsp.quality.infra.converter.WorkOrderConverter;
import org.xdsp.quality.infra.mapper.WorkOrderMapper;
import org.xdsp.quality.infra.util.CustomThreadPool;
import org.xdsp.quality.infra.util.DataSecurityUtil;
import org.xdsp.quality.message.adapter.WorkOrderMessageAdapter;

import java.util.*;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.Collectors;

import static org.apache.poi.util.LocaleID.ZH_CN;
import static org.xdsp.quality.infra.constant.WorkOrderConstants.OrderOperateType;
import static org.xdsp.quality.infra.constant.WorkOrderConstants.WorkOrderStatus;

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
    private final WorkOrderMessageAdapter workOrderMessageAdapter;
    private final XdspProfileClient profileClient;
    private final FlyBookHelper flyBookHelper;

    public static final String WORK_ORDER_CODE = "XQUA.WORK_ORDER_CODE";
    private static final String GLOBAL = "GLOBAL";
    /**
     * 质量工单代办消息发送消息配置
     */
    public static final String ORDER_LAUNCH = "XQUA.ORDER_LAUNCH";
    /**
     * 质量工单提交消息发送消息配置
     */
    public static final String ORDER_SUBMIT = "XQUA.ORDER_SUBMIT";
    /**
     *质量工单拒绝消息发送消息配置
     */
    public static final String ORDER_REFUSE = "XQUA.ORDER_REFUSE";
    /**
     * 质量工单催办消息发送消息配置
     */
    public static final String ORDER_TODO = "XQUA.ORDER_TODO";
    /**
     * 质量工单转交消息发送配置
     */
    public static final String ORDER_FORWARD = "XQUA.ORDER_FORWARD";
    private static final String WORK_ORDER_REMINDER_WAYS = "XQUA.WORK_ORDER_REMINDER_WAYS";
    public static final String FLY_BOOK = "FLY_BOOK";
    public static final String FLY_BOOK_PHONE = "FLY_BOOK_PHONE";

    public WorkOrderServiceImpl(WorkOrderRepository workOrderRepository,
                                WorkOrderOperationRepository workOrderOperationRepository,
                                CodeRuleBuilder codeRuleBuilder,
                                WorkOrderMapper workOrderMapper,
                                WorkOrderConverter workOrderConverter,
                                MessageClient messageClient,
                                WorkOrderMessageAdapter workOrderMessageAdapter,
                                XdspProfileClient profileClient,
                                FlyBookHelper flyBookHelper) {
        this.workOrderRepository = workOrderRepository;
        this.workOrderOperationRepository = workOrderOperationRepository;
        this.codeRuleBuilder = codeRuleBuilder;
        this.workOrderMapper = workOrderMapper;
        this.workOrderConverter = workOrderConverter;
        this.messageClient = messageClient;
        this.workOrderMessageAdapter = workOrderMessageAdapter;
        this.profileClient = profileClient;
        this.flyBookHelper = flyBookHelper;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<WorkOrderDTO> launchUpdate(List<WorkOrderDTO> workOrderDTOList) {
        //发起整改
        workOrderDTOList.forEach(workOrderDTO -> {
            //如果是工单被拒绝，可重新发起整改
            if (Objects.nonNull(workOrderDTO.getWorkOrderId())) {
                WorkOrderDTO oldOrderDTO = workOrderRepository.selectDTOByPrimaryKey(workOrderDTO.getWorkOrderId());
                workOrderDTO.setWorkOrderCode(oldOrderDTO.getWorkOrderCode());
                workOrderDTO.setObjectVersionNumber(oldOrderDTO.getObjectVersionNumber());
            } else {
                //根据planId,resultId去查询，一个方案的一次评估结果只能发起一次整改(状态不是已拒绝的)
                Optional<WorkOrderDTO> any = workOrderRepository.selectDTOByCondition(Condition.builder(WorkOrder.class)
                        .andWhere(Sqls.custom()
                                .andEqualTo(WorkOrder.FIELD_PLAN_ID, workOrderDTO.getPlanId())
                                .andEqualTo(WorkOrder.FIELD_RESULT_ID, workOrderDTO.getResultId())
                                .andNotEqualTo(WorkOrder.FIELD_WORK_ORDER_STATUS, WorkOrderStatus.REFUSED))
                        .build()).stream().findAny();
                if (any.isPresent()) {
                    //已经发起过质量工单
                    throw new CommonException(ErrorCode.WORK_ORDER_ALREADY_LAUNCH);
                }

                //生成质量工单编码
                workOrderDTO.setWorkOrderCode(codeRuleBuilder.generateCode(DetailsHelper.getUserDetails().getTenantId(), WORK_ORDER_CODE, GLOBAL, GLOBAL, null));
            }

            //默认为待接收状态
            workOrderDTO.setWorkOrderStatus(WorkOrderStatus.PENDING_RECEIVE);
        });

        //记录整改操作
        List<WorkOrderOperationDTO> workOrderOperationDTOList = new ArrayList<>();
        workOrderDTOList.forEach(workOrderDTO -> {
            if (Objects.nonNull(workOrderDTO.getWorkOrderId())) {
                //重新整改则更新
                workOrderRepository.updateDTOAllColumnWhereTenant(workOrderDTO, workOrderDTO.getTenantId());
            } else {
                //此处不使用批量插入了，批量插入接受的返回值会覆盖掉前端传递的其他参数
                workOrderRepository.insertDTOSelective(workOrderDTO);
            }
            WorkOrderOperationDTO workOrderOperationDTO = WorkOrderOperationDTO.builder()
                    .workOrderId(workOrderDTO.getWorkOrderId())
                    .workOrderStatus(WorkOrderStatus.PENDING_RECEIVE)
                    //创建人即为发起操作人
                    .operatorId(DetailsHelper.getUserDetails().getUserId())
                    .operateType(OrderOperateType.LAUNCH)
                    //操作描述 ${方案}需整改，紧急程度为${XX}
                    .processComment(String.format("【%s】需整改，紧急程度为【%s】", workOrderDTO.getWorkOrderCode(), workOrderDTO.getImmediateLevelMeaning()))
                    .build();
            workOrderOperationDTOList.add(workOrderOperationDTO);
        });
        workOrderOperationRepository.batchInsertDTOSelective(workOrderOperationDTOList);
        //异步发起发送消息（消息不影响主流程的运行）
        ThreadPoolExecutor executor = CustomThreadPool.getExecutor();
        CustomUserDetails userDetails = DetailsHelper.getUserDetails();
        executor.submit(() -> sendMessage(workOrderDTOList, ORDER_LAUNCH, userDetails));
        return workOrderDTOList;
    }

    private void sendMessage(List<WorkOrderDTO> workOrderDTOList, String messageTemplateCode, CustomUserDetails userDetails) {
        DetailsHelper.setCustomUserDetails(userDetails);
        Long tenantId = userDetails.getTenantId();
        String receiverWays = profileClient.getProfileValue(WORK_ORDER_REMINDER_WAYS);
        boolean containsFlyBook = StringUtils.contains(receiverWays, FLY_BOOK) || StringUtils.contains(receiverWays, FLY_BOOK_PHONE);
        List<String> receiverWayList = new ArrayList<>();
        if (StringUtils.isNotBlank(receiverWays)) {
            receiverWayList = Arrays.stream(receiverWays.split(BaseConstants.Symbol.COMMA)).collect(Collectors.toList());
        }
        if (CollectionUtils.isEmpty(receiverWayList)) {
            receiverWayList.add("WEB");
        }
        Map<Long, Receiver> receiverMap = new HashMap<>();
        List<String> mobiles = new ArrayList<>();
        for (WorkOrderDTO workOrderDTO : workOrderDTOList) {
            Receiver receiver = new Receiver();
            if (ORDER_LAUNCH.equals(messageTemplateCode) || ORDER_FORWARD.equals(messageTemplateCode)) {
                receiver.setUserId(workOrderDTO.getProcessorsId());
            } else {
                receiver.setUserId(workOrderDTO.getCreatedBy());
            }
            receiver.setEmail(acquireEmail(receiver.getUserId()));
            if (containsFlyBook) {
                String phone = acquirePhone(receiver.getUserId());
                receiver.setPhone(phone);
                mobiles.add(phone);
            }
            receiver.setTargetUserTenantId(workOrderDTO.getTenantId());
            receiverMap.put(workOrderDTO.getWorkOrderId(), receiver);
        }
        mobiles = mobiles.stream().distinct().collect(Collectors.toList());
        Map<String, String> flyBookUserIdMap;
        if (CollectionUtils.isNotEmpty(mobiles)) {
            flyBookUserIdMap = flyBookHelper.batchGetUserIdByMobiles(mobiles);
        } else {
            flyBookUserIdMap = new HashMap<>();
        }
        for (WorkOrderDTO workOrderDTO : workOrderDTOList) {
            //模板内容 您发起的${工单号}工单已被拒绝，可确认后重新提交。
            Map<String, String> args = new HashMap<>();
            args.put("workOrderCode", workOrderDTO.getWorkOrderCode());
            Receiver receiver = receiverMap.get(workOrderDTO.getWorkOrderId());
            List<String> tmpReceiverWayList = new ArrayList<>(receiverWayList);
            FlyBookSender flyBookSender = null;
            if (containsFlyBook) {
                String phone = receiver.getPhone();
                String flyBookOpenUserId = null;
                if (StringUtils.isNotBlank(phone)) {
                    flyBookOpenUserId = flyBookUserIdMap.get(phone);
                }
                if (tmpReceiverWayList.contains(FLY_BOOK)) {
                    if (StringUtils.isNotBlank(flyBookOpenUserId)) {
                        Map<String, String> openUserId = new HashMap<>();
                        openUserId.put("user_id", flyBookOpenUserId);
                        flyBookSender = new FlyBookSender()
                                .setMsg_type(FlyBookMsgType.POST.getValue())
                                .setReceiverList(Collections.singletonList(openUserId))
                                .setArgs(new HashMap<>(args))
                                .setMessageCode(messageTemplateCode)
                                .setServerCode(messageTemplateCode)
                                .setTenantId(tenantId);
                    }
                    tmpReceiverWayList.remove(FLY_BOOK);
                }
                if (tmpReceiverWayList.contains(FLY_BOOK_PHONE)) {
                    if (StringUtils.isNotBlank(flyBookOpenUserId)) {
                        if (flyBookSender == null) {
                            Map<String, String> openUserId = new HashMap<>();
                            openUserId.put("user_id", flyBookOpenUserId);
                            flyBookSender = new FlyBookSender()
                                    .setMsg_type(FlyBookMsgType.POST.getValue())
                                    .setReceiverList(Collections.singletonList(openUserId))
                                    .setArgs(new HashMap<>(args))
                                    .setMessageCode(messageTemplateCode)
                                    .setServerCode(messageTemplateCode)
                                    .setTenantId(tenantId)
                                    .setUrgentFlag(BaseConstants.Flag.YES)
                                    .setUrgentType(FlyBookUrgentType.PHONE.getValue());
                        } else {
                            flyBookSender.setUrgentFlag(BaseConstants.Flag.YES)
                                    .setUrgentType(FlyBookUrgentType.PHONE.getValue());
                        }
                    }
                    tmpReceiverWayList.remove(FLY_BOOK_PHONE);
                }
            }
            MessageSender messageSender = null;
            if (CollectionUtils.isNotEmpty(tmpReceiverWayList)) {
                messageSender = MessageSender.builder()
                        .typeCodeList(tmpReceiverWayList)
                        .messageCode(messageTemplateCode)
                        .serverCode(messageTemplateCode)
                        .tenantId(tenantId)
                        .receiverAddressList(Collections.singletonList(receiver))
                        .args(args)
                        .lang(ZH_CN.getLanguageTag())
                        .build();
            }
            if (messageSender != null || flyBookSender != null) {
                messageClient.sendMessage(messageSender, null, null, null, flyBookSender);
            }
            if (messageSender != null) {
                //消息适配器
                workOrderMessageAdapter.sendMessage(messageSender);
            }
            if (flyBookSender != null) {
                //消息适配器
                workOrderMessageAdapter.sendMessage(flyBookSender);
            }
        }
    }

    private String acquirePhone(Long processorsId) {
        String phone = "";
        String encryptedPhone = workOrderMapper.findUserPhone(processorsId);
        if(!Strings.isNullOrEmpty(encryptedPhone)){
            phone = DataSecurityUtil.decrypt(encryptedPhone);
        }
        return phone;
    }

    private String acquireEmail(Long processorsId) {
        String email = "";
        String encryptedEmail = workOrderMapper.findUserEmail(processorsId);
        if(!Strings.isNullOrEmpty(encryptedEmail)){
             email = DataSecurityUtil.decrypt(encryptedEmail);
        }
        return email;
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
                .filter(workOrderDTO -> !WorkOrderStatus.PENDING_RECEIVE.equals(workOrderDTO.getWorkOrderStatus()))
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
                    .workOrderStatus(workOrderStatus)
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
            //异步发起发送消息（消息不影响主流程的运行）
            ThreadPoolExecutor executor = CustomThreadPool.getExecutor();
            CustomUserDetails userDetails = DetailsHelper.getUserDetails();
            executor.submit(() -> sendMessage(workOrderDTOList, ORDER_REFUSE, userDetails));
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
        WorkOrderDTO workOrderDTO = detail(workOrderId);
        //按照操作日志升序排序，取得最后一个操作的动作。增加下一动作的虚拟节点
        List<WorkOrderOperationDTO> workOrderOperationDTOList = workOrderMapper.oderOperateInfo(workOrderId);
        if (CollectionUtils.isEmpty(workOrderOperationDTOList)) {
            return workOrderOperationDTOList;
        }
        WorkOrderOperationDTO workOrderOperationDTO = workOrderOperationDTOList.get(workOrderOperationDTOList.size() - 1);
        String operateType = workOrderOperationDTO.getOperateType();
        WorkOrderOperationDTO operationDTO = null;

        switch (operateType) {
            //
            case OrderOperateType.LAUNCH:
                operationDTO = WorkOrderOperationDTO.builder()
                        .operatorName(workOrderDTO.getProcessorName())
                        .operateType(OrderOperateType.RECEIVE)
                        .processComment("质量工单已下发，请数据对象所属部门尽快处理")
                        .build();
                break;
            case OrderOperateType.RECEIVE:
                operationDTO = WorkOrderOperationDTO.builder()
                        .operatorName(workOrderDTO.getProcessorName())
                        .operateType(OrderOperateType.START_PROCESS)
                        .processComment("质量工单已接收，请质量整改人员尽快修复问题数据并反馈")
                        .build();
                break;
            case OrderOperateType.START_PROCESS:
                operationDTO = WorkOrderOperationDTO.builder()
                        .operatorName(workOrderDTO.getProcessorName())
                        .operateType(OrderOperateType.SUBMIT_SOLUTION)
                        .build();
                break;
            case OrderOperateType.ASSIGN:
                //判断前置动作有没有开始处理
                Optional<WorkOrderOperationDTO> any = workOrderOperationDTOList.stream()
                        .filter(dto -> OrderOperateType.START_PROCESS.equals(dto.getOperateType()))
                        .findAny();
                //如果有，则后置为提交
                if (any.isPresent()) {
                    operationDTO = WorkOrderOperationDTO.builder()
                            .operatorName(workOrderDTO.getProcessorName())
                            .operateType(OrderOperateType.SUBMIT_SOLUTION)
                            .build();
                } else {
                    operationDTO = WorkOrderOperationDTO.builder()
                            .operatorName(workOrderDTO.getProcessorName())
                            .operateType(OrderOperateType.START_PROCESS)
                            .build();
                }
        }
        if (operationDTO != null) {
            workOrderOperationDTOList.add(operationDTO);
        }
        return workOrderOperationDTOList;
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
        //设置为处理中
        workOrderDTO.setWorkOrderStatus(WorkOrderStatus.PROCESSING);
        //操作记录为开始处理
        WorkOrderOperationDTO workOrderOperationDTO = WorkOrderOperationDTO.builder()
                .workOrderId(workOrderId)
                .workOrderStatus(WorkOrderStatus.PROCESSING)
                .operatorId(DetailsHelper.getUserDetails().getUserId())
                .operateType(OrderOperateType.START_PROCESS)
                .build();
        workOrderRepository.updateDTOOptional(workOrderDTO, WorkOrder.FIELD_WORK_ORDER_STATUS);
        workOrderOperationRepository.insertDTOSelective(workOrderOperationDTO);
        return workOrderDTO;
    }

    @Override
    public WorkOrderDTO orderAssign(WorkOrderDTO workOrderDTO) {
        //转交质量工单
        //转交不调整工单状态，更改处理人
        workOrderDTO.setProcessorsId(workOrderDTO.getAssignId());

        //操作记录为开始处理
        WorkOrderOperationDTO workOrderOperationDTO = WorkOrderOperationDTO.builder()
                .workOrderId(workOrderDTO.getWorkOrderId())
                .workOrderStatus(workOrderDTO.getWorkOrderStatus())
                .operatorId(DetailsHelper.getUserDetails().getUserId())
                .operateType(OrderOperateType.ASSIGN)
                .processComment(String.format("转交给%s", workOrderDTO.getAssignName()))
                .build();
        workOrderRepository.updateDTOOptional(workOrderDTO, WorkOrder.FIELD_PROCESSORS_ID);
        workOrderOperationRepository.insertDTOSelective(workOrderOperationDTO);

        //异步发起发送消息（消息不影响主流程的运行）
        ThreadPoolExecutor executor = CustomThreadPool.getExecutor();
        CustomUserDetails userDetails = DetailsHelper.getUserDetails();
        executor.submit(() -> sendMessage(Collections.singletonList(workOrderDTO), ORDER_FORWARD, userDetails));
        return workOrderDTO;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public WorkOrderDTO orderSubmit(WorkOrderDTO workOrderDTO) {
        if (workOrderDTO == null || StringUtils.isEmpty(workOrderDTO.getOrderSolution())) {
            throw new CommonException(ErrorCode.WORK_ORDER_SOLUTION_CAN_NOT_NULL);
        }

        //修改状态为已处理
        workOrderDTO.setWorkOrderStatus(WorkOrderStatus.PROCESSED);

        //记录提交解决方案的操作日志
        WorkOrderOperationDTO workOrderOperationDTO = WorkOrderOperationDTO.builder()
                .workOrderId(workOrderDTO.getWorkOrderId())
                .workOrderStatus(WorkOrderStatus.PROCESSED)
                .operatorId(DetailsHelper.getUserDetails().getUserId())
                .operateType(OrderOperateType.SUBMIT_SOLUTION)
                .processComment("提交解决方案")
                .build();


        workOrderRepository.updateDTOOptional(workOrderDTO, WorkOrder.FIELD_WORK_ORDER_STATUS);
        workOrderOperationRepository.insertDTOSelective(workOrderOperationDTO);

        //异步发起发送消息（消息不影响主流程的运行）
        ThreadPoolExecutor executor = CustomThreadPool.getExecutor();
        CustomUserDetails userDetails = DetailsHelper.getUserDetails();
        executor.submit(() -> sendMessage(Collections.singletonList(workOrderDTO), ORDER_SUBMIT, userDetails));
        return workOrderDTO;
    }

    @Override
    public WorkOrderDTO orderRemind(Long workOrderId) {
        WorkOrderDTO workOrderDTO = workOrderRepository.selectDTOByPrimaryKey(workOrderId);
        sendMessage(Collections.singletonList(workOrderDTO), ORDER_TODO, DetailsHelper.getUserDetails());
        return workOrderDTO;
//        Long tenantId = DetailsHelper.getUserDetails().getTenantId();
//        WorkOrderDTO workOrderDTO = workOrderRepository.selectDTOByPrimaryKey(workOrderId);
//        //处理人
//        Long processorsId = workOrderDTO.getProcessorsId();
//        //接受组为发起人
//        Receiver receiver = new Receiver();
//        receiver.setUserId(processorsId).setTargetUserTenantId(tenantId);
//        //解密获取用户邮箱
//        String encryptedEmail = workOrderMapper.findUserEmail(processorsId);
//        if(!Strings.isNullOrEmpty(encryptedEmail)){
//            String email = DataSecurityUtil.decrypt(encryptedEmail);
//            receiver.setEmail(email);
//        }
//        //模板内容 您发起的${工单号}工单已被拒绝，可确认后重新提交。
//        //您有数据质量整改工单${workOrderCode}待处理，请及时处理！！！
//        Map<String, String> args = new HashMap<>();
//        args.put("workOrderCode", workOrderDTO.getWorkOrderCode());
//        //发送自定义消息，可设置邮件/站内消息发送开关
//        MessageSender messageSender = MessageSender.builder()
//                .tenantId(workOrderDTO.getTenantId())
//                .messageCode(ORDER_TODO)
//                .serverCode(ORDER_TODO)
//                .lang(ZH_CN.getLanguageTag())
//                .receiverAddressList(Collections.singletonList(receiver))
//                .args(args)
//                .build();
//        messageClient.sendMessage(messageSender);
//        //消息适配器
//        workOrderMessageAdapter.sendMessage(messageSender);
//        return workOrderDTO;
    }

    @Override
    public WorkOrderDTO detail(Long workOrderId) {
        return workOrderMapper.detail(workOrderId);
    }

}
