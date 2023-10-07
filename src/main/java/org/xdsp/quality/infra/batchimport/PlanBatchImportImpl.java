package org.xdsp.quality.infra.batchimport;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.choerodon.core.oauth.DetailsHelper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.hzero.boot.imported.app.service.BatchImportHandler;
import org.hzero.boot.imported.app.service.IBatchImportService;
import org.hzero.boot.imported.infra.validator.annotation.ImportService;
import org.hzero.mybatis.domian.Condition;
import org.hzero.mybatis.util.Sqls;
import org.xdsp.core.util.ProjectHelper;
import org.xdsp.quality.api.dto.BatchPlanDTO;
import org.xdsp.quality.api.dto.PlanGroupDTO;
import org.xdsp.quality.domain.entity.BatchPlan;
import org.xdsp.quality.domain.entity.PlanGroup;
import org.xdsp.quality.domain.repository.BatchPlanRepository;
import org.xdsp.quality.domain.repository.PlanGroupRepository;
import org.xdsp.quality.infra.constant.TemplateCodeConstants;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * description
 * </p>
 *
 * @author lgl 2021/03/11 14:55
 * @since 1.0
 */
@Slf4j
@ImportService(templateCode = TemplateCodeConstants.TEMPLATE_CODE_BATCH_PLAN, sheetIndex = 1)
public class PlanBatchImportImpl extends BatchImportHandler implements IBatchImportService {
    private final ObjectMapper objectMapper;

    private final BatchPlanRepository batchPlanRepository;

    private final PlanGroupRepository planGroupRepository;

    public PlanBatchImportImpl(ObjectMapper objectMapper, BatchPlanRepository batchPlanRepository, PlanGroupRepository planGroupRepository) {
        this.objectMapper = objectMapper;
        this.batchPlanRepository = batchPlanRepository;
        this.planGroupRepository = planGroupRepository;
    }


    @Override
    public Boolean doImport(List<String> data) {
        // 设置租户Id
        Long tenantId = DetailsHelper.getUserDetails().getTenantId();
        Long projectId = ProjectHelper.getProjectId();
        List<BatchPlanDTO> plans = new ArrayList<>(data.size());
        try {
            for (String json : data) {
                BatchPlanDTO batchPlan = objectMapper.readValue(json, BatchPlanDTO.class);
                batchPlan.setTenantId(tenantId);
                batchPlan.setProjectId(projectId);
                plans.add(batchPlan);
            }
        } catch (IOException e) {
            // 失败
            log.error("Permission Object data:{}", data);
            log.error("Permission Object Read Json Error", e);
            return false;
        }
        List<BatchPlanDTO> insertBatchPlanDTOList = new ArrayList<>();
        int index = 0;
        for (BatchPlanDTO batchPlan : plans) {
            List<PlanGroupDTO> planGroupDTOList = planGroupRepository.selectDTOByCondition(Condition.builder(PlanGroup.class)
                    .andWhere(Sqls.custom()
                            .andEqualTo(PlanGroup.FIELD_GROUP_CODE, batchPlan.getGroupCode())
                            .andEqualTo(PlanGroup.FIELD_TENANT_ID, batchPlan.getTenantId())
                            .andEqualTo(PlanGroup.FIELD_PROJECT_ID, batchPlan.getProjectId()))
                    .build());
            if (CollectionUtils.isNotEmpty(planGroupDTOList)) {
                batchPlan.setGroupId(planGroupDTOList.get(0).getGroupId());
            } else {
                addErrorMsg(index, String.format("方案分组【%s】不存在", batchPlan.getGroupCode()));
            }

            BatchPlan plan = batchPlanRepository.selectOne(BatchPlan.builder().planCode(batchPlan.getPlanCode())
                    .tenantId(batchPlan.getTenantId())
                    .projectId(batchPlan.getProjectId())
                    .build());
            //如果评估方案已存在
            if (plan != null) {
                batchPlan.setPlanId(plan.getPlanId());
                batchPlan.setObjectVersionNumber(plan.getObjectVersionNumber());
                batchPlanRepository.updateByDTOPrimaryKey(batchPlan);
            } else {
                insertBatchPlanDTOList.add(batchPlan);
            }
            index++;
        }
        batchPlanRepository.batchInsertDTOSelective(insertBatchPlanDTOList);
        return true;
    }
}
