package com.hand.hdsp.quality.infra.workflow;

import com.hand.hdsp.quality.api.dto.StandardApprovalDTO;
import com.hand.hdsp.quality.app.service.StandardApprovalService;
import com.hand.hdsp.quality.domain.entity.Root;
import com.hand.hdsp.quality.domain.repository.RootRepository;
import com.hand.hdsp.quality.infra.constant.WorkFlowConstant;
import com.hand.hdsp.quality.infra.util.AnsjUtil;
import com.hand.hdsp.workflow.common.infra.quality.RootOfflineWorkflowAdapter;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static com.hand.hdsp.quality.infra.constant.StandardConstant.Status.*;

import io.choerodon.core.oauth.DetailsHelper;

import org.hzero.boot.workflow.WorkflowClient;
import org.hzero.boot.workflow.constant.WorkflowConstant;
import org.hzero.boot.workflow.dto.RunInstance;

/**
 * description
 *
 * @author XIN.SHENG01@HAND-CHINA.COM 2023/01/09 11:20
 */
@Component
public class DefaultRootOfflineWorkflowAdapter implements RootOfflineWorkflowAdapter<Root,Root,Root,Root> {
    private final StandardApprovalService standardApprovalService;
    private final WorkflowClient workflowClient;

    public DefaultRootOfflineWorkflowAdapter(StandardApprovalService standardApprovalService, WorkflowClient workflowClient) {
        this.standardApprovalService = standardApprovalService;
        this.workflowClient = workflowClient;
    }

    @Override
    public Root startWorkflow(Root root) {
        Long userId = DetailsHelper.getUserDetails().getUserId();
        StandardApprovalDTO standardApprovalDTO = StandardApprovalDTO
                .builder()
                .standardId(root.getId())
                .standardType("ROOT")
                .applicantId(userId)
                .applyType(OFFLINE)
                .tenantId(root.getTenantId())
                .build();
        // 删除原来的审批记录
        standardApprovalService.delete(StandardApprovalDTO
                .builder()
                .standardId(root.getId())
                .standardType("ROOT")
                .applicantId(userId)
                .build());
        standardApprovalDTO = standardApprovalService.createOrUpdate(standardApprovalDTO);
        //使用当前时间戳作为业务主键
        String bussinessKey = String.valueOf(System.currentTimeMillis());
        Map<String, Object> var = new HashMap<>();
        //给流程变量
        var.put("rootId", root.getId());
        var.put("approvalId", standardApprovalDTO.getApprovalId());
        //使用自研工作流客户端
        RunInstance runInstance = workflowClient.startInstanceByFlowKey(root.getTenantId(),
                WorkFlowConstant.Root.OFFLINE_WORKFLOW_KEY, bussinessKey, "USER", String.valueOf(userId), var);
        if (Objects.nonNull(runInstance)) {
            standardApprovalDTO.setApplyTime(runInstance.getStartDate());
            standardApprovalDTO.setInstanceId(runInstance.getInstanceId());
            standardApprovalService.createOrUpdate(standardApprovalDTO);
        }
        return root;
    }

    @Override
    public Root callBack(Root root, String approveResult) {
        if (root != null) {
            if (WorkflowConstant.ApproveAction.APPROVED.equals(approveResult)) {
                root.setReleaseStatus(OFFLINE);
            } else {
                root.setReleaseStatus(ONLINE);
            }
        }
        return root;
    }
}
