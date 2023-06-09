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
import org.xdsp.quality.api.dto.BatchPlanBaseDTO;
import org.xdsp.quality.api.dto.BatchPlanRelTableDTO;
import org.xdsp.quality.domain.entity.BatchPlanBase;
import org.xdsp.quality.domain.entity.BatchPlanRelTable;
import org.xdsp.quality.domain.repository.BatchPlanBaseRepository;
import org.xdsp.quality.domain.repository.BatchPlanRelTableRepository;
import org.xdsp.quality.infra.constant.TemplateCodeConstants;
import org.xdsp.quality.infra.mapper.BatchPlanRelTableMapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * description
 * </p>
 *
 * @author lgl 2021/03/11 15:14
 * @since 1.0
 */
@Slf4j
@ImportService(templateCode = TemplateCodeConstants.TEMPLATE_CODE_BATCH_PLAN, sheetIndex = 5)
public class PlanRelTableBatchImportImpl extends BatchImportHandler implements IBatchImportService {

    private final ObjectMapper objectMapper;

    private final BatchPlanBaseRepository batchPlanBaseRepository;

    private final BatchPlanRelTableRepository batchPlanRelTableRepository;

    private final BatchPlanRelTableMapper batchPlanRelTableMapper;

    public PlanRelTableBatchImportImpl(ObjectMapper objectMapper, BatchPlanBaseRepository batchPlanBaseRepository, BatchPlanRelTableRepository batchPlanRelTableRepository, BatchPlanRelTableMapper batchPlanRelTableMapper) {
        this.objectMapper = objectMapper;
        this.batchPlanBaseRepository = batchPlanBaseRepository;
        this.batchPlanRelTableRepository = batchPlanRelTableRepository;
        this.batchPlanRelTableMapper = batchPlanRelTableMapper;
    }

    @Override
    public Boolean doImport(List<String> data) {
        // 设置租户Id
        Long tenantId = DetailsHelper.getUserDetails().getTenantId();
        Long projectId = ProjectHelper.getProjectId();
        List<BatchPlanRelTableDTO> batchPlanRelTableDTOList = new ArrayList<>(data.size());
        try {
            int index = 0;
            for (String json : data) {
                BatchPlanRelTableDTO batchPlanRelTableDTO = objectMapper.readValue(json, BatchPlanRelTableDTO.class);
                batchPlanRelTableDTO.setExceptionBlock("是".equals(batchPlanRelTableDTO.getExceptionBlockFlag()) ? 1 : 0);
                batchPlanRelTableDTO.setTenantId(tenantId);
                batchPlanRelTableDTO.setProjectId(projectId);
                //查询planBaseId
                List<BatchPlanBaseDTO> batchPlanBaseDTOS = batchPlanBaseRepository.selectDTOByCondition(Condition.builder(BatchPlanBase.class)
                        .andWhere(Sqls.custom()
                                .andEqualTo(BatchPlanBase.FIELD_PLAN_BASE_CODE, batchPlanRelTableDTO.getPlanBaseCode())
                                .andEqualTo(BatchPlanBase.FIELD_TENANT_ID, batchPlanRelTableDTO.getTenantId())
                                .andEqualTo(BatchPlanBase.FIELD_PROJECT_ID, batchPlanRelTableDTO.getProjectId()))
                        .build());
                if (CollectionUtils.isNotEmpty(batchPlanBaseDTOS)) {
                    batchPlanRelTableDTO.setPlanBaseId(batchPlanBaseDTOS.get(0).getPlanBaseId());
                } else {
                    addErrorMsg(index, String.format("质检项编码【%s】不存在", batchPlanRelTableDTO.getPlanBaseCode()));
                    return false;
                }
                //根据datasourceCode查询datasoureId和类型
                BatchPlanRelTableDTO dto = batchPlanRelTableMapper.selectDatasourceIdAndType(batchPlanRelTableDTO);
                batchPlanRelTableDTO.setRelDatasourceId(dto.getRelDatasourceId());
                batchPlanRelTableDTO.setRelDatasourceType(dto.getRelDatasourceType());
                batchPlanRelTableDTOList.add(batchPlanRelTableDTO);
                index++;
            }
        } catch (IOException e) {
            // 失败
            log.error("Permission Object data:{}", data);
            log.error("Permission Object Read Json Error", e);
            return false;
        }
        List<BatchPlanRelTableDTO> insertList = new ArrayList<>();
        batchPlanRelTableDTOList.forEach(relTableDTO -> {
            BatchPlanRelTable batchPlanRelTable = batchPlanRelTableRepository.selectOne(BatchPlanRelTable.builder()
                    .planBaseId(relTableDTO.getPlanBaseId())
                    .ruleCode(relTableDTO.getRuleCode())
                    .build());
            if (batchPlanRelTable != null) {
                relTableDTO.setPlanRuleId(batchPlanRelTable.getPlanRuleId());
                relTableDTO.setObjectVersionNumber(batchPlanRelTable.getObjectVersionNumber());
                batchPlanRelTableRepository.updateByDTOPrimaryKey(relTableDTO);
            } else {
                insertList.add(relTableDTO);
            }
        });
        batchPlanRelTableRepository.batchInsertDTOSelective(insertList);
        return true;
    }
}
