package com.hand.hdsp.quality.infra.workflow;

import com.hand.hdsp.quality.api.dto.StandardApprovalDTO;
import com.hand.hdsp.quality.app.service.RootService;
import com.hand.hdsp.quality.app.service.StandardApprovalService;
import com.hand.hdsp.quality.domain.entity.Root;
import com.hand.hdsp.quality.domain.entity.RootLine;
import com.hand.hdsp.quality.domain.repository.RootLineRepository;
import com.hand.hdsp.quality.domain.repository.RootRepository;
import com.hand.hdsp.quality.infra.constant.WorkFlowConstant;
import com.hand.hdsp.quality.infra.util.AnsjUtil;
import com.hand.hdsp.workflow.common.infra.OnlineWorkflowAdapter;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.logging.log4j.util.Strings;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.hand.hdsp.quality.infra.constant.StandardConstant.Status.*;

import io.choerodon.core.oauth.DetailsHelper;

import org.hzero.boot.workflow.WorkflowClient;
import org.hzero.boot.workflow.constant.WorkflowConstant;
import org.hzero.boot.workflow.dto.RunInstance;

/**
 * description
 *
 * @author XIN.SHENG01@HAND-CHINA.COM 2023/01/09 10:45
 */
@Component
public class RootOnlineWorkflowAdapter implements OnlineWorkflowAdapter<Root,Root,Long,Root> {

    private final StandardApprovalService standardApprovalService;
    private final WorkflowClient workflowClient;
    private final RootService rootService;
    private final RootRepository rootRepository;
    private final RootLineRepository rootLineRepository;
    private final AnsjUtil ansjUtil;

    public RootOnlineWorkflowAdapter(StandardApprovalService standardApprovalService, WorkflowClient workflowClient, RootService rootService, RootRepository rootRepository, RootLineRepository rootLineRepository, AnsjUtil ansjUtil) {
        this.standardApprovalService = standardApprovalService;
        this.workflowClient = workflowClient;
        this.rootService = rootService;
        this.rootRepository = rootRepository;
        this.rootLineRepository = rootLineRepository;
        this.ansjUtil = ansjUtil;
    }

    @Override
    public Root startWorkflow(Root root) {
        Long userId = DetailsHelper.getUserDetails().getUserId();
        //修改状态
        root.setReleaseStatus(ONLINE_APPROVING);
        rootRepository.updateOptional(root, Root.FIELD_RELEASE_STATUS);
        StandardApprovalDTO standardApprovalDTO = StandardApprovalDTO
                .builder()
                .standardId(root.getId())
                .standardType("ROOT")
                .applicantId(userId)
                .applyType(ONLINE)
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
                WorkFlowConstant.Root.ONLINE_WORKFLOW_KEY, bussinessKey, "USER", String.valueOf(userId), var);
        if (Objects.nonNull(runInstance)) {
            standardApprovalDTO.setApplyTime(runInstance.getStartDate());
            standardApprovalDTO.setInstanceId(runInstance.getInstanceId());
            standardApprovalService.createOrUpdate(standardApprovalDTO);
        }
        return root;
    }

    @Override
    public Root callBack(Long rootId, String approveResult) {
        Root root = rootRepository.selectByPrimaryKey(rootId);
        if (root != null) {
            if (WorkflowConstant.ApproveAction.APPROVED.equals(approveResult)) {
                root.setReleaseStatus(ONLINE);
            } else {
                root.setReleaseStatus(OFFLINE);
            }
            rootRepository.updateOptional(root, Root.FIELD_RELEASE_STATUS);
            if (ONLINE.equals(root.getReleaseStatus())) {
                rootService.doVersion(root);
                //上线后词库追加词根对应的中文
                List<RootLine> rootLines = rootLineRepository.select(RootLine.builder().rootId(rootId).build());
                if (CollectionUtils.isNotEmpty(rootLines)) {
                    List<String> addWords = rootLines.stream()
                            .filter(rootLine -> Strings.isNotBlank(rootLine.getRootName()))
                            .map(RootLine::getRootName)
                            .distinct()
                            .collect(Collectors.toList());
                    ansjUtil.addWord(root.getTenantId(), root.getProjectId(), addWords);
                }
            }
        }
        return root;
    }
}
