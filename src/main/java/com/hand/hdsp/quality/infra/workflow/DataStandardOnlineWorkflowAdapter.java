package com.hand.hdsp.quality.infra.workflow;

import com.hand.hdsp.quality.api.dto.DataStandardDTO;
import com.hand.hdsp.quality.api.dto.StandardAimDTO;
import com.hand.hdsp.quality.api.dto.StandardApprovalDTO;
import com.hand.hdsp.quality.app.service.DataStandardService;
import com.hand.hdsp.quality.app.service.StandardApprovalService;
import com.hand.hdsp.quality.domain.entity.DataStandard;
import com.hand.hdsp.quality.domain.entity.StandardAim;
import com.hand.hdsp.quality.domain.repository.DataStandardRepository;
import com.hand.hdsp.quality.domain.repository.StandardAimRepository;
import com.hand.hdsp.quality.infra.constant.WorkFlowConstant;
import com.hand.hdsp.quality.infra.feign.AssetFeign;
import com.hand.hdsp.workflow.common.infra.OnlineWorkflowAdapter;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import static com.hand.hdsp.quality.infra.constant.StandardConstant.StandardType.DATA;
import static com.hand.hdsp.quality.infra.constant.StandardConstant.Status.*;

import io.choerodon.core.oauth.DetailsHelper;

import org.hzero.boot.workflow.WorkflowClient;
import org.hzero.boot.workflow.constant.WorkflowConstant;
import org.hzero.boot.workflow.dto.RunInstance;
import org.hzero.mybatis.domian.Condition;
import org.hzero.mybatis.util.Sqls;

/**
 * description
 *
 * @author XIN.SHENG01@HAND-CHINA.COM 2023/01/10 9:09
 */
@Component
public class DataStandardOnlineWorkflowAdapter implements OnlineWorkflowAdapter<DataStandardDTO,DataStandardDTO,String,DataStandardDTO> {

    private final DataStandardRepository dataStandardRepository;

    private final StandardApprovalService standardApprovalService;

    private final WorkflowClient workflowClient;

    private final StandardAimRepository standardAimRepository;

    private final DataStandardService dataStandardService;

    @Resource
    private AssetFeign assetFeign;

    public DataStandardOnlineWorkflowAdapter(DataStandardRepository dataStandardRepository, StandardApprovalService standardApprovalService, WorkflowClient workflowClient, StandardAimRepository standardAimRepository, DataStandardService dataStandardService) {
        this.dataStandardRepository = dataStandardRepository;
        this.standardApprovalService = standardApprovalService;
        this.workflowClient = workflowClient;
        this.standardAimRepository = standardAimRepository;
        this.dataStandardService = dataStandardService;
    }

    @Override
    public DataStandardDTO startWorkflow(DataStandardDTO dataStandardDTO) {
        //修改状态
        dataStandardDTO.setStandardStatus(ONLINE_APPROVING);
        dataStandardRepository.updateDTOOptional(dataStandardDTO, DataStandard.FIELD_STANDARD_STATUS);

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
    public DataStandardDTO callBack(String dataStandardCode, String approveResult) {
        Long tenantId = DetailsHelper.getUserDetails().getTenantId();
        List<DataStandardDTO> standardDTOS = dataStandardRepository.selectDTOByCondition(Condition.builder(DataStandard.class)
                .andWhere(Sqls.custom()
                        .andEqualTo(DataStandard.FIELD_TENANT_ID, tenantId)
                        .andEqualTo(DataStandard.FIELD_STANDARD_CODE, dataStandardCode))
                .build());
        if(WorkflowConstant.ApproveAction.APPROVED.equals(approveResult)){
            if (CollectionUtils.isNotEmpty(standardDTOS)) {
                DataStandardDTO dataStandardDTO = standardDTOS.get(0);
                dataStandardDTO.setStandardStatus(ONLINE);
                dataStandardRepository.updateDTOOptional(dataStandardDTO, DataStandard.FIELD_STANDARD_STATUS);
                //存版本表
                dataStandardService.doVersion(dataStandardDTO);
                //1.数据标准没有关联评估方案，直接发布，不做处理
                //2.数据标准关联了评估方案，第一次发布，则落标到数据质量生成规则
                //3.数据标准关联了评估方案，不是第一发布，则更新落标到数据质量的规则
                //查看此标准落标表的情况
                List<StandardAimDTO> standardAimDTOS = standardAimRepository.selectDTOByCondition(Condition.builder(StandardAim.class)
                        .andWhere(Sqls.custom()
                                .andEqualTo(StandardAim.FIELD_STANDARD_ID, dataStandardDTO.getStandardId())
                                .andEqualTo(StandardAim.FIELD_STANDARD_TYPE, DATA)
                                .andEqualTo(StandardAim.FIELD_TENANT_ID, dataStandardDTO.getTenantId()))
                        .build());
                if (CollectionUtils.isNotEmpty(standardAimDTOS)) {
                    //过滤出关联了评估方案的落标
                    List<StandardAimDTO> aimDTOS = standardAimDTOS.stream()
                            .filter(s -> Objects.nonNull(s.getPlanId()))
                            .collect(Collectors.toList());
                    dataStandardService.publishRelatePlan(aimDTOS);
                }
                assetFeign.saveStandardToEs(dataStandardDTO.getTenantId(), dataStandardDTO);
            }
        }else{
            if (CollectionUtils.isNotEmpty(standardDTOS)) {
                DataStandardDTO dataStandardDTO = standardDTOS.get(0);
                dataStandardDTO.setStandardStatus(OFFLINE);
                dataStandardRepository.updateDTOOptional(dataStandardDTO, DataStandard.FIELD_STANDARD_STATUS);
            }
        }
        return null;
    }
}
