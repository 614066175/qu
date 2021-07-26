package com.hand.hdsp.quality.infra.batchimport;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hand.hdsp.quality.api.dto.BatchPlanBaseDTO;
import com.hand.hdsp.quality.api.dto.BatchPlanRelTableDTO;
import com.hand.hdsp.quality.domain.entity.BatchPlanBase;
import com.hand.hdsp.quality.domain.repository.BatchPlanBaseRepository;
import com.hand.hdsp.quality.domain.repository.BatchPlanRelTableRepository;
import com.hand.hdsp.quality.infra.constant.TemplateCodeConstants;
import com.hand.hdsp.quality.infra.mapper.BatchPlanRelTableMapper;
import io.choerodon.core.oauth.DetailsHelper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.hzero.boot.imported.app.service.IBatchImportService;
import org.hzero.boot.imported.infra.validator.annotation.ImportService;
import org.hzero.mybatis.domian.Condition;
import org.hzero.mybatis.util.Sqls;

/**
 * <p>
 * description
 * </p>
 *
 * @author lgl 2021/03/11 15:14
 * @since 1.0
 */
@Slf4j
@ImportService(templateCode = TemplateCodeConstants.TEMPLATE_CODE_BATCH_PLAN,sheetIndex = 5)
public class PlanRelTableBatchImportImpl implements IBatchImportService {

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
        List<BatchPlanRelTableDTO> batchPlanRelTableDTOList = new ArrayList<>(data.size());
        try {
            for (String json : data) {
                BatchPlanRelTableDTO batchPlanRelTableDTO = objectMapper.readValue(json, BatchPlanRelTableDTO.class);
                batchPlanRelTableDTO.setExceptionBlock("是".equals(batchPlanRelTableDTO.getExceptionBlockFlag())?1:0);
                batchPlanRelTableDTO.setTenantId(tenantId);
                //查询planBaseId
                List<BatchPlanBaseDTO> batchPlanBaseDTOS = batchPlanBaseRepository.selectDTOByCondition(Condition.builder(BatchPlanBase.class)
                        .andWhere(Sqls.custom()
                                .andEqualTo(BatchPlanBase.FIELD_PLAN_BASE_CODE, batchPlanRelTableDTO.getPlanBaseCode())
                                .andEqualTo(BatchPlanBase.FIELD_TENANT_ID, batchPlanRelTableDTO.getTenantId()))
                        .build());
                if(CollectionUtils.isNotEmpty(batchPlanBaseDTOS)){
                    batchPlanRelTableDTO.setPlanBaseId(batchPlanBaseDTOS.get(0).getPlanBaseId());
                }
                //根据datasourceCode查询datasoureId和类型
                BatchPlanRelTableDTO dto = batchPlanRelTableMapper.selectDatasourceIdAndType(batchPlanRelTableDTO);
                batchPlanRelTableDTO.setRelDatasourceId(dto.getRelDatasourceId());
                batchPlanRelTableDTO.setRelDatasourceType(dto.getRelDatasourceType());
                batchPlanRelTableDTOList.add(batchPlanRelTableDTO);
            }
        } catch (IOException e) {
            // 失败
            log.error("Permission Object data:{}", data);
            log.error("Permission Object Read Json Error", e);
            return false;
        }
        batchPlanRelTableRepository.batchInsertDTOSelective(batchPlanRelTableDTOList);
        return true;
    }
}