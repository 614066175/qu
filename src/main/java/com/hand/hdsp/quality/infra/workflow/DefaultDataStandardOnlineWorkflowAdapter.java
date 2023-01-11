package com.hand.hdsp.quality.infra.workflow;

import com.hand.hdsp.quality.api.dto.DataStandardDTO;
import com.hand.hdsp.quality.api.dto.StandardApprovalDTO;
import com.hand.hdsp.quality.app.service.DataStandardService;
import com.hand.hdsp.quality.app.service.StandardApprovalService;
import com.hand.hdsp.quality.domain.repository.StandardAimRepository;
import com.hand.hdsp.quality.infra.constant.WorkFlowConstant;
import com.hand.hdsp.quality.infra.feign.AssetFeign;
import com.hand.hdsp.workflow.common.infra.quality.DataStandardOnlineWorkflowAdapter;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import javax.annotation.Resource;

import static com.hand.hdsp.quality.infra.constant.StandardConstant.Status.*;

import io.choerodon.core.oauth.DetailsHelper;

import org.hzero.boot.workflow.WorkflowClient;
import org.hzero.boot.workflow.constant.WorkflowConstant;
import org.hzero.boot.workflow.dto.RunInstance;

/**
 * description
 *
 * @author XIN.SHENG01@HAND-CHINA.COM 2023/01/10 9:09
 */
@Component
public class DefaultDataStandardOnlineWorkflowAdapter implements DataStandardOnlineWorkflowAdapter<DataStandardDTO,DataStandardDTO,String,String> {

    private final StandardApprovalService standardApprovalService;

    private final WorkflowClient workflowClient;

    @Resource
    private AssetFeign assetFeign;

    public DefaultDataStandardOnlineWorkflowAdapter(StandardApprovalService standardApprovalService, WorkflowClient workflowClient, StandardAimRepository standardAimRepository, DataStandardService dataStandardService) {
        this.standardApprovalService = standardApprovalService;
        this.workflowClient = workflowClient;
    }

    @Override
    public DataStandardDTO startWorkflow(DataStandardDTO dataStandardDTO) {
        Long userId = DetailsHelper.getUserDetails().getUserId();
        StandardApprovalDTO standardApprovalDTO = StandardApprovalDTO
                .builder()
                .standardId(dataStandardDTO.getStandardId())
                .standardType("DATA")
                .applicantId(userId)
                .applyType(ONLINE)
                .tenantId(dataStandardDTO.getTenantId())
                .build();
        // 先删除原来的审批记录
        standardApprovalService.delete(StandardApprovalDTO
                .builder()
                .standardId(dataStandardDTO.getStandardId())
                .standardType("DATA")
                .applicantId(userId)
                .build());
        standardApprovalDTO = standardApprovalService.createOrUpdate(standardApprovalDTO);
        //使用当前时间戳作为业务主键
        String bussinessKey = String.valueOf(System.currentTimeMillis());
        Map<String, Object> var = new HashMap<>();
        //给流程变量
        var.put("dataStandardCode", dataStandardDTO.getStandardCode());
        var.put("approvalId", standardApprovalDTO.getApprovalId());
        //使用自研工作流客户端
        RunInstance runInstance = workflowClient.startInstanceByFlowKey(dataStandardDTO.getTenantId(), WorkFlowConstant.DataStandard.ONLINE_WORKFLOW_KEY, bussinessKey, "USER", String.valueOf(DetailsHelper.getUserDetails().getUserId()), var);
//        workFlowFeign.startInstanceByFlowKey(dataStandardDTO.getTenantId(), workflowKey, bussinessKey, "USER",String.valueOf(DetailsHelper.getUserDetails().getUserId()), var);
        if (Objects.nonNull(runInstance)) {
            standardApprovalDTO.setApplyTime(runInstance.getStartDate());
            standardApprovalDTO.setInstanceId(runInstance.getInstanceId());
            standardApprovalService.createOrUpdate(standardApprovalDTO);
        }
        return dataStandardDTO;
    }

    @Override
    public String callBack(String dataStandardCode, String approveResult) {
        if(WorkflowConstant.ApproveAction.APPROVED.equals(approveResult)){
            return WorkflowConstant.ApproveAction.APPROVED;
        }
        return WorkflowConstant.ApproveAction.REJECTED;
    }
}
