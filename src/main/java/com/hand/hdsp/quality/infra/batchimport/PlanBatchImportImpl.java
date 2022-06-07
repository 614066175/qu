package com.hand.hdsp.quality.infra.batchimport;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hand.hdsp.core.util.ProjectHelper;
import com.hand.hdsp.quality.api.dto.BatchPlanDTO;
import com.hand.hdsp.quality.api.dto.PlanGroupDTO;
import com.hand.hdsp.quality.domain.entity.PlanGroup;
import com.hand.hdsp.quality.domain.repository.BatchPlanRepository;
import com.hand.hdsp.quality.domain.repository.PlanGroupRepository;
import com.hand.hdsp.quality.infra.constant.TemplateCodeConstants;
import io.choerodon.core.oauth.DetailsHelper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.hzero.boot.imported.app.service.IBatchImportService;
import org.hzero.boot.imported.infra.validator.annotation.ImportService;
import org.hzero.mybatis.domian.Condition;
import org.hzero.mybatis.util.Sqls;

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
public class PlanBatchImportImpl implements IBatchImportService {
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
        plans.forEach(batchPlan -> {
            List<PlanGroupDTO> planGroupDTOList = planGroupRepository.selectDTOByCondition(Condition.builder(PlanGroup.class)
                    .andWhere(Sqls.custom()
                            .andEqualTo(PlanGroup.FIELD_GROUP_CODE, batchPlan.getGroupCode())
                            .andEqualTo(PlanGroup.FIELD_TENANT_ID, batchPlan.getTenantId())
                            .andEqualTo(PlanGroup.FIELD_PROJECT_ID, batchPlan.getProjectId()))
                    .build());
            if (CollectionUtils.isNotEmpty(planGroupDTOList)) {
                batchPlan.setGroupId(planGroupDTOList.get(0).getGroupId());
            }
        });
        batchPlanRepository.batchInsertDTOSelective(plans);
        return true;
    }
}
