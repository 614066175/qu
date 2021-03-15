package com.hand.hdsp.quality.infra.batchimport;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hand.hdsp.quality.api.dto.BatchPlanBaseDTO;
import com.hand.hdsp.quality.api.dto.BatchPlanFieldConDTO;
import com.hand.hdsp.quality.api.dto.BatchPlanFieldDTO;
import com.hand.hdsp.quality.api.dto.BatchPlanFieldLineDTO;
import com.hand.hdsp.quality.domain.entity.BatchPlanBase;
import com.hand.hdsp.quality.domain.repository.BatchPlanBaseRepository;
import com.hand.hdsp.quality.domain.repository.BatchPlanFieldConRepository;
import com.hand.hdsp.quality.domain.repository.BatchPlanFieldLineRepository;
import com.hand.hdsp.quality.domain.repository.BatchPlanFieldRepository;
import com.hand.hdsp.quality.infra.constant.TemplateCodeConstants;
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
 * @author lgl 2021/03/11 15:13
 * @since 1.0
 */
@Slf4j
@ImportService(templateCode = TemplateCodeConstants.TEMPLATE_CODE_BATCH_PLAN,sheetIndex = 4)
public class PlanFieldBatchImportImpl implements IBatchImportService {

    private final ObjectMapper objectMapper;

    private final BatchPlanFieldRepository batchPlanFieldRepository;

    private final BatchPlanFieldLineRepository batchPlanFieldLineRepository;

    private final BatchPlanFieldConRepository batchPlanFieldConRepository;

    private final BatchPlanBaseRepository batchPlanBaseRepository;

    public PlanFieldBatchImportImpl(ObjectMapper objectMapper, BatchPlanFieldRepository batchPlanFieldRepository, BatchPlanFieldLineRepository batchPlanFieldLineRepository, BatchPlanFieldConRepository batchPlanFieldConRepository, BatchPlanBaseRepository batchPlanBaseRepository) {
        this.objectMapper = objectMapper;
        this.batchPlanFieldRepository = batchPlanFieldRepository;
        this.batchPlanFieldLineRepository = batchPlanFieldLineRepository;
        this.batchPlanFieldConRepository = batchPlanFieldConRepository;
        this.batchPlanBaseRepository = batchPlanBaseRepository;
    }


    @Override
    public Boolean doImport(List<String> data) {
        // 设置租户Id
        Long tenantId = DetailsHelper.getUserDetails().getTenantId();
        List<BatchPlanFieldDTO> batchPlanFieldDTOS = new ArrayList<>(data.size());
        try {
            for (String json : data) {
                BatchPlanFieldDTO batchPlanFieldDTO = objectMapper.readValue(json, BatchPlanFieldDTO.class);
                batchPlanFieldDTO.setExceptionBlock("是".equals(batchPlanFieldDTO.getExceptionBlockFlag())?1:0);
                batchPlanFieldDTO.setTenantId(tenantId);
                //查询planBaseId
                List<BatchPlanBaseDTO> batchPlanBaseDTOS = batchPlanBaseRepository.selectDTOByCondition(Condition.builder(BatchPlanBase.class)
                        .andWhere(Sqls.custom()
                                .andEqualTo(BatchPlanBase.FIELD_PLAN_BASE_CODE, batchPlanFieldDTO.getPlanBaseCode())
                                .andEqualTo(BatchPlanBase.FIELD_TENANT_ID, batchPlanFieldDTO.getTenantId()))
                        .build());
                if(CollectionUtils.isNotEmpty(batchPlanBaseDTOS)){
                    batchPlanFieldDTO.setPlanBaseId(batchPlanBaseDTOS.get(0).getPlanBaseId());
                }
                batchPlanFieldDTOS.add(batchPlanFieldDTO);
            }
        } catch (IOException e) {
            // 失败
            log.error("Permission Object data:{}", data);
            log.error("Permission Object Read Json Error", e);
            return false;
        }
        //插入表级规则,获取返回主键
        List<BatchPlanFieldDTO> fieldDTOS = batchPlanFieldRepository.batchInsertDTOSelective(batchPlanFieldDTOS);
        //插入规则行
        List<BatchPlanFieldLineDTO> fieldLineDTOList=new ArrayList<>();
        for(int i = 0; i < batchPlanFieldDTOS.size(); i++) {
            BatchPlanFieldDTO dto = batchPlanFieldDTOS.get(i);
            BatchPlanFieldLineDTO fieldLineDTO = BatchPlanFieldLineDTO.builder()
                    .planRuleId(fieldDTOS.get(i).getPlanRuleId())
                    .checkWay(dto.getCheckWay())
                    .checkItem(dto.getCheckItem())
                    .countType(dto.getCountType())
                    .fieldName(dto.getFieldName())
                    .checkFieldName(dto.getCheckFieldName())
                    .regularExpression(dto.getRegularExpression())
                    .tenantId(dto.getTenantId())
                    .build();
            fieldLineDTOList.add(fieldLineDTO);
        }
        //插入规则行，获取返回主键
        List<BatchPlanFieldLineDTO> lineList = batchPlanFieldLineRepository.batchInsertDTOSelective(fieldLineDTOList);
        List<BatchPlanFieldConDTO> fieldConDTOList=new ArrayList<>();
        for(int i = 0; i < batchPlanFieldDTOS.size(); i++) {
            BatchPlanFieldDTO dto = batchPlanFieldDTOS.get(i);
            BatchPlanFieldConDTO fieldConDTO = BatchPlanFieldConDTO.builder()
                    .planLineId(lineList.get(i).getPlanLineId())
                    .whereCondition(dto.getWhereCondition())
                    .compareWay(dto.getCompareWay())
                    .warningLevel(dto.getWarningLevel())
                    .tenantId(dto.getTenantId())
                    .build();
            fieldConDTOList.add(fieldConDTO);
        }
        //插入告警配置
        batchPlanFieldConRepository.batchInsertDTOSelective(fieldConDTOList);
        return true;
    }
}
