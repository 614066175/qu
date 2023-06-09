package org.xdsp.quality.infra.workflow;

import io.choerodon.core.oauth.DetailsHelper;
import org.hzero.boot.workflow.WorkflowClient;
import org.hzero.boot.workflow.constant.WorkflowConstant;
import org.hzero.boot.workflow.dto.RunInstance;
import org.springframework.stereotype.Component;
import org.xdsp.quality.api.dto.StandardApprovalDTO;
import org.xdsp.quality.app.service.impl.StandardApprovalServiceImpl;
import org.xdsp.quality.domain.entity.Root;
import org.xdsp.quality.infra.constant.WorkFlowConstant;
import org.xdsp.quality.infra.util.ApplicationContextUtil;
import org.xdsp.quality.workflow.adapter.RootOfflineWorkflowAdapter;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static org.xdsp.quality.infra.constant.StandardConstant.Status.OFFLINE;
import static org.xdsp.quality.infra.constant.StandardConstant.Status.ONLINE;

/**
 * description
 *
 * @author XIN.SHENG01@HAND-CHINA.COM 2023/01/09 11:20
 */
@Component
public class DefaultRootOfflineWorkflowAdapter implements RootOfflineWorkflowAdapter<Root,Root,Root,Root> {
    private final WorkflowClient workflowClient;

    public DefaultRootOfflineWorkflowAdapter(WorkflowClient workflowClient) {
        this.workflowClient = workflowClient;
    }

    @Override
    public Root startWorkflow(Root root) {
        StandardApprovalServiceImpl standardApprovalService = ApplicationContextUtil.findBean(StandardApprovalServiceImpl.class);
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
        var.put("chargeId", root.getChargeId());
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
