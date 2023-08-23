package org.xdsp.quality.infra.workflow;

import io.choerodon.core.oauth.DetailsHelper;
import org.hzero.boot.workflow.WorkflowClient;
import org.hzero.boot.workflow.constant.WorkflowConstant;
import org.hzero.boot.workflow.dto.RunInstance;
import org.springframework.stereotype.Component;
import org.xdsp.quality.api.dto.DataStandardDTO;
import org.xdsp.quality.api.dto.StandardApprovalDTO;
import org.xdsp.quality.app.service.impl.StandardApprovalServiceImpl;
import org.xdsp.quality.infra.constant.WorkFlowConstant;
import org.xdsp.quality.infra.feign.AssetFeign;
import org.xdsp.quality.infra.util.ApplicationContextUtil;
import org.xdsp.quality.workflow.adapter.DataStandardOfflineWorkflowAdapter;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static org.xdsp.quality.infra.constant.StandardConstant.Status.OFFLINE;

/**
 * description
 *
 * @author XIN.SHENG01@HAND-CHINA.COM 2023/01/10 9:24
 */
@Component
public class DefaultDataStandardOfflineWorkflowAdapter implements DataStandardOfflineWorkflowAdapter<DataStandardDTO,DataStandardDTO,String,String> {

    private final WorkflowClient workflowClient;

    @Resource
    private AssetFeign assetFeign;

    public DefaultDataStandardOfflineWorkflowAdapter(WorkflowClient workflowClient) {
        this.workflowClient = workflowClient;
    }

    @Override
    public DataStandardDTO startWorkflow(DataStandardDTO dataStandardDTO) {
        StandardApprovalServiceImpl standardApprovalService = ApplicationContextUtil.findBean(StandardApprovalServiceImpl.class);
        Long userId = DetailsHelper.getUserDetails().getUserId();
        StandardApprovalDTO standardApprovalDTO = StandardApprovalDTO
                .builder()
                .standardId(dataStandardDTO.getStandardId())
                .standardType("DATA")
                .applicantId(userId)
                .applyType(OFFLINE)
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
        RunInstance runInstance = workflowClient.startInstanceByFlowKey(dataStandardDTO.getTenantId(), WorkFlowConstant.DataStandard.OFFLINE_WORKFLOW_KEY, bussinessKey, "USER", String.valueOf(DetailsHelper.getUserDetails().getUserId()), var);
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
