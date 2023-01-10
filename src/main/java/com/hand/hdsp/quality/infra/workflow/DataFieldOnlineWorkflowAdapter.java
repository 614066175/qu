package com.hand.hdsp.quality.infra.workflow;

import com.hand.hdsp.quality.api.dto.DataFieldDTO;
import com.hand.hdsp.quality.api.dto.StandardApprovalDTO;
import com.hand.hdsp.quality.app.service.DataFieldService;
import com.hand.hdsp.quality.app.service.StandardApprovalService;
import com.hand.hdsp.quality.domain.entity.DataStandard;
import com.hand.hdsp.quality.domain.repository.DataFieldRepository;
import com.hand.hdsp.quality.infra.constant.WorkFlowConstant;
import com.hand.hdsp.workflow.common.infra.OfflineWorkflowAdapter;
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
 * @author XIN.SHENG01@HAND-CHINA.COM 2023/01/09 11:55
 */
@Component
public class DataFieldOnlineWorkflowAdapter  implements OfflineWorkflowAdapter<DataFieldDTO,DataFieldDTO,Long,DataFieldDTO> {
    private final StandardApprovalService standardApprovalService;
    private final WorkflowClient workflowClient;
    private final DataFieldRepository dataFieldRepository;
    private final DataFieldService dataFieldService;

    public DataFieldOnlineWorkflowAdapter(StandardApprovalService standardApprovalService, WorkflowClient workflowClient, DataFieldRepository dataFieldRepository, DataFieldService dataFieldService) {
        this.standardApprovalService = standardApprovalService;
        this.workflowClient = workflowClient;
        this.dataFieldRepository = dataFieldRepository;
        this.dataFieldService = dataFieldService;
    }

    @Override
    public DataFieldDTO startWorkflow(DataFieldDTO dataFieldDTO) {
        //指定字段标准修改状态
        dataFieldDTO.setStandardStatus(ONLINE_APPROVING);
        dataFieldRepository.updateDTOOptional(dataFieldDTO, DataStandard.FIELD_STANDARD_STATUS);

        Long userId = DetailsHelper.getUserDetails().getUserId();
        StandardApprovalDTO standardApprovalDTO = StandardApprovalDTO
                .builder()
                .standardId(dataFieldDTO.getFieldId())
                .standardType("FIELD")
                .applicantId(userId)
                .applyType(ONLINE)
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
        //使用自研工作流客户端
        RunInstance runInstance = workflowClient.startInstanceByFlowKey(dataFieldDTO.getTenantId(), WorkFlowConstant.FieldStandard.ONLINE_WORKFLOW_KEY, bussinessKey, "USER", String.valueOf(userId), var);
        if (Objects.nonNull(runInstance)) {
            standardApprovalDTO.setApplyTime(runInstance.getStartDate());
            standardApprovalDTO.setInstanceId(runInstance.getInstanceId());
            standardApprovalService.createOrUpdate(standardApprovalDTO);
        }
        return dataFieldDTO;
    }

    @Override
    public DataFieldDTO callBack(Long fieldId, String approveResult) {
        DataFieldDTO dataFieldDTO = dataFieldRepository.selectDTOByPrimaryKey(fieldId);
        if (dataFieldDTO != null) {
            if(WorkflowConstant.ApproveAction.APPROVED.equals(approveResult)){
                dataFieldDTO.setStandardStatus(ONLINE);
            }else{
                dataFieldDTO.setStandardStatus(OFFLINE);
            }
            dataFieldRepository.updateDTOOptional(dataFieldDTO, DataStandard.FIELD_STANDARD_STATUS);
            if (ONLINE.equals(dataFieldDTO.getStandardStatus())) {
                dataFieldService.doVersion(dataFieldDTO);
            }
        }
        return dataFieldDTO;
    }
}
