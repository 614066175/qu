package org.xdsp.quality.infra.workflow;

import io.choerodon.core.oauth.CustomUserDetails;
import io.choerodon.core.oauth.DetailsHelper;
import org.hzero.boot.workflow.WorkflowClient;
import org.hzero.boot.workflow.constant.WorkflowConstant;
import org.hzero.boot.workflow.dto.RunInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.xdsp.quality.api.dto.DataFieldDTO;
import org.xdsp.quality.api.dto.StandardApprovalDTO;
import org.xdsp.quality.app.service.impl.StandardApprovalServiceImpl;
import org.xdsp.quality.infra.constant.WorkFlowConstant;
import org.xdsp.quality.infra.util.ApplicationContextUtil;
import org.xdsp.quality.workflow.adapter.DataFieldOfflineWorkflowAdapter;
import org.xdsp.workflow.common.external.ExternalFlowHelper;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static org.xdsp.quality.infra.constant.StandardConstant.Status.OFFLINE;

/**
 * description
 *
 * @author XIN.SHENG01@HAND-CHINA.COM 2023/01/09 20:08
 */
@Component
public class DefaultDataFieldOfflineWorkflowAdapter implements DataFieldOfflineWorkflowAdapter<DataFieldDTO,DataFieldDTO,Long,String> {
    private final WorkflowClient workflowClient;

    @Autowired
    private ExternalFlowHelper externalFlowHelper;

    public DefaultDataFieldOfflineWorkflowAdapter(WorkflowClient workflowClient) {
        this.workflowClient = workflowClient;
    }

    @Override
    public DataFieldDTO startWorkflow(DataFieldDTO dataFieldDTO) {
        StandardApprovalServiceImpl standardApprovalService = ApplicationContextUtil.findBean(StandardApprovalServiceImpl.class);
        Long userId = DetailsHelper.getUserDetails().getUserId();
        StandardApprovalDTO standardApprovalDTO = StandardApprovalDTO
                .builder()
                .standardId(dataFieldDTO.getFieldId())
                .standardType("FIELD")
                .applicantId(userId)
                .applyType(OFFLINE)
                .tenantId(dataFieldDTO.getTenantId())
                .build();
        // 先删除原来的审批记录
        standardApprovalService.delete(StandardApprovalDTO
                .builder()
                .standardId(dataFieldDTO.getFieldId())
                .standardType("FIELD")
                .applicantId(userId)
                .build());
        standardApprovalDTO = standardApprovalService.createOrUpdate(standardApprovalDTO);
        //使用当前时间戳作为业务主键
        String bussinessKey = String.valueOf(System.currentTimeMillis());
        Map<String, Object> var = new HashMap<>();
        //给流程变量
        var.put("fieldId", dataFieldDTO.getFieldId());
        var.put("approvalId", standardApprovalDTO.getApprovalId());
        CustomUserDetails self = DetailsHelper.getUserDetails();
        StandardApprovalDTO finalStandardApprovalDTO = standardApprovalDTO;
        externalFlowHelper.startUserInstance(self.getTenantNum(),
                WorkFlowConstant.ExternalFieldStandard.OFFLINE_WORKFLOW_KEY,
                self.getUsername(),
                var,
                () -> {
                    //使用自研工作流客户端
                    RunInstance runInstance = workflowClient.startInstanceByFlowKey(dataFieldDTO.getTenantId(), WorkFlowConstant.FieldStandard.OFFLINE_WORKFLOW_KEY, bussinessKey, "USER", String.valueOf(userId), var);
                    if (Objects.nonNull(runInstance)) {
                        finalStandardApprovalDTO.setApplyTime(runInstance.getStartDate());
                        finalStandardApprovalDTO.setInstanceId(runInstance.getInstanceId());
                        standardApprovalService.createOrUpdate(finalStandardApprovalDTO);
                    }
                });

        return dataFieldDTO;
    }

    @Override
    public String callBack(Long fieldId, String approveResult) {
        if(WorkflowConstant.ApproveAction.APPROVED.equals(approveResult)){
            return WorkflowConstant.ApproveAction.APPROVED;
        }
        return WorkflowConstant.ApproveAction.REJECTED;
    }
}
