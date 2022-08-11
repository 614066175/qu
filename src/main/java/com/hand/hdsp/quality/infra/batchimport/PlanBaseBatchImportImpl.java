package com.hand.hdsp.quality.infra.batchimport;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hand.hdsp.core.util.ProjectHelper;
import com.hand.hdsp.quality.api.dto.BatchPlanBaseDTO;
import com.hand.hdsp.quality.api.dto.BatchPlanDTO;
import com.hand.hdsp.quality.domain.entity.BatchPlan;
import com.hand.hdsp.quality.domain.entity.BatchPlanBase;
import com.hand.hdsp.quality.domain.repository.BatchPlanBaseRepository;
import com.hand.hdsp.quality.domain.repository.BatchPlanRepository;
import com.hand.hdsp.quality.infra.constant.TemplateCodeConstants;
import io.choerodon.core.oauth.DetailsHelper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.hzero.boot.driver.infra.context.PluginDatasourceHelper;
import org.hzero.boot.imported.app.service.IBatchImportService;
import org.hzero.boot.imported.infra.validator.annotation.ImportService;
import org.hzero.mybatis.domian.Condition;
import org.hzero.mybatis.util.Sqls;
import org.hzero.starter.driver.core.infra.vo.PluginDatasourceVO;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * description
 * </p>
 *
 * @author lgl 2021/03/11 15:01
 * @since 1.0
 */
@Slf4j
@ImportService(templateCode = TemplateCodeConstants.TEMPLATE_CODE_BATCH_PLAN, sheetIndex = 2)
public class PlanBaseBatchImportImpl implements IBatchImportService {
    private final ObjectMapper objectMapper;

    private final BatchPlanBaseRepository batchPlanBaseRepository;

    private final BatchPlanRepository batchPlanRepository;

    private final PluginDatasourceHelper pluginDatasourceHelper;

    public PlanBaseBatchImportImpl(ObjectMapper objectMapper, BatchPlanBaseRepository batchPlanBaseRepository, BatchPlanRepository batchPlanRepository, PluginDatasourceHelper pluginDatasourceHelper) {
        this.objectMapper = objectMapper;
        this.batchPlanBaseRepository = batchPlanBaseRepository;
        this.batchPlanRepository = batchPlanRepository;
        this.pluginDatasourceHelper = pluginDatasourceHelper;
    }


    @Override
    public Boolean doImport(List<String> data) {
        // 设置租户Id
        Long tenantId = DetailsHelper.getUserDetails().getTenantId();
        Long projectId = ProjectHelper.getProjectId();
        List<BatchPlanBaseDTO> batchPlanBaseList = new ArrayList<>(data.size());
        try {
            for (String json : data) {
                BatchPlanBaseDTO batchPlanBaseDTO = objectMapper.readValue(json, BatchPlanBaseDTO.class);
                batchPlanBaseDTO.setTenantId(tenantId);
                batchPlanBaseDTO.setProjectId(projectId);
                batchPlanBaseList.add(batchPlanBaseDTO);
            }
        } catch (IOException e) {
            // 失败
            log.error("Permission Object data:{}", data);
            log.error("Permission Object Read Json Error", e);
            return false;
        }
        List<BatchPlanBaseDTO> insertBatchPlanBaseList=new ArrayList<>();
        batchPlanBaseList.forEach(batchPlanBaseDTO -> {
            List<BatchPlanDTO> batchPlanDTOList = batchPlanRepository.selectDTOByCondition(Condition.builder(BatchPlan.class)
                    .andWhere(Sqls.custom()
                            .andEqualTo(BatchPlan.FIELD_PLAN_CODE, batchPlanBaseDTO.getPlanCode())
                            .andEqualTo(BatchPlan.FIELD_TENANT_ID, batchPlanBaseDTO.getTenantId())
                            .andEqualTo(BatchPlan.FIELD_PROJECT_ID, batchPlanBaseDTO.getProjectId()))
                    .build());
            if (CollectionUtils.isNotEmpty(batchPlanDTOList)) {
                batchPlanBaseDTO.setPlanId(batchPlanDTOList.get(0).getPlanId());
            }
            BatchPlanBase batchPlanBase = batchPlanBaseRepository.selectOne(BatchPlanBase.builder()
                    .planBaseCode(batchPlanBaseDTO.getPlanBaseCode())
                    .tenantId(batchPlanBaseDTO.getTenantId())
                    .projectId(batchPlanBaseDTO.getProjectId())
                    .build());
            PluginDatasourceVO pluginDatasourceVO = pluginDatasourceHelper.getPluginDatasourceVO(tenantId, batchPlanBaseDTO.getDatasourceCode());
            batchPlanBaseDTO.setDatasourceId(pluginDatasourceVO.getDatasourceId());
            if (batchPlanBase != null) {
                batchPlanBaseDTO.setPlanBaseId(batchPlanBase.getPlanBaseId());
                batchPlanBaseDTO.setObjectVersionNumber(batchPlanBase.getObjectVersionNumber());
                batchPlanBaseRepository.updateByDTOPrimaryKeySelective(batchPlanBaseDTO);
            }else{
                insertBatchPlanBaseList.add(batchPlanBaseDTO);
            }
        });
        batchPlanBaseRepository.batchInsertDTOSelective(insertBatchPlanBaseList);
        return true;
    }
}
