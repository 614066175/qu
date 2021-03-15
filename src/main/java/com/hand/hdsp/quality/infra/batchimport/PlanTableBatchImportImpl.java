package com.hand.hdsp.quality.infra.batchimport;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hand.hdsp.quality.api.dto.BatchPlanBaseDTO;
import com.hand.hdsp.quality.api.dto.BatchPlanTableConDTO;
import com.hand.hdsp.quality.api.dto.BatchPlanTableDTO;
import com.hand.hdsp.quality.api.dto.BatchPlanTableLineDTO;
import com.hand.hdsp.quality.domain.entity.BatchPlanBase;
import com.hand.hdsp.quality.domain.repository.BatchPlanBaseRepository;
import com.hand.hdsp.quality.domain.repository.BatchPlanTableConRepository;
import com.hand.hdsp.quality.domain.repository.BatchPlanTableLineRepository;
import com.hand.hdsp.quality.domain.repository.BatchPlanTableRepository;
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
 * @author lgl 2021/03/11 15:14
 * @since 1.0
 */
@Slf4j
@ImportService(templateCode = TemplateCodeConstants.TEMPLATE_CODE_BATCH_PLAN,sheetIndex = 3)
public class PlanTableBatchImportImpl implements IBatchImportService {

    private final ObjectMapper objectMapper;

    private final BatchPlanTableRepository batchPlanTableRepository;

    private final BatchPlanTableLineRepository batchPlanTableLineRepository;

    private final BatchPlanTableConRepository batchPlanTableConRepository;

    private final BatchPlanBaseRepository batchPlanBaseRepository;

    public PlanTableBatchImportImpl(ObjectMapper objectMapper, BatchPlanTableRepository batchPlanTableRepository, BatchPlanTableLineRepository batchPlanTableLineRepository, BatchPlanTableConRepository batchPlanTableConRepository, BatchPlanBaseRepository batchPlanBaseRepository) {
        this.objectMapper = objectMapper;
        this.batchPlanTableRepository = batchPlanTableRepository;
        this.batchPlanTableLineRepository = batchPlanTableLineRepository;
        this.batchPlanTableConRepository = batchPlanTableConRepository;
        this.batchPlanBaseRepository = batchPlanBaseRepository;
    }

    @Override
    public Boolean doImport(List<String> data) {
        // 设置租户Id
        Long tenantId = DetailsHelper.getUserDetails().getTenantId();
        List<BatchPlanTableDTO> batchPlanTableDTOS = new ArrayList<>(data.size());
        try {
            for (String json : data) {
                BatchPlanTableDTO batchPlanTableDTO = objectMapper.readValue(json, BatchPlanTableDTO.class);
                batchPlanTableDTO.setExceptionBlock("是".equals(batchPlanTableDTO.getExceptionBlockFlag())?1:0);
                batchPlanTableDTO.setTenantId(tenantId);
                //查询planBaseId
                List<BatchPlanBaseDTO> batchPlanBaseDTOS = batchPlanBaseRepository.selectDTOByCondition(Condition.builder(BatchPlanBase.class)
                        .andWhere(Sqls.custom()
                                .andEqualTo(BatchPlanBase.FIELD_PLAN_BASE_CODE, batchPlanTableDTO.getPlanBaseCode())
                                .andEqualTo(BatchPlanBase.FIELD_TENANT_ID, batchPlanTableDTO.getTenantId()))
                        .build());
                if(CollectionUtils.isNotEmpty(batchPlanBaseDTOS)){
                    batchPlanTableDTO.setPlanBaseId(batchPlanBaseDTOS.get(0).getPlanBaseId());
                }
                batchPlanTableDTOS.add(batchPlanTableDTO);
            }
        } catch (IOException e) {
            // 失败
            log.error("Permission Object data:{}", data);
            log.error("Permission Object Read Json Error", e);
            return false;
        }
        //插入表级规则,获取返回主键
        List<BatchPlanTableDTO> tableList = batchPlanTableRepository.batchInsertDTOSelective(batchPlanTableDTOS);
        //插入规则行
        List<BatchPlanTableLineDTO> tableLineDTOList=new ArrayList<>();
        for(int i = 0; i < batchPlanTableDTOS.size(); i++) {
            BatchPlanTableDTO dto = batchPlanTableDTOS.get(i);
            BatchPlanTableLineDTO tableLineDTO = BatchPlanTableLineDTO.builder()
                    .planRuleId(tableList.get(i).getPlanRuleId())
                    .checkItem(dto.getCheckItem())
                    .countType(dto.getCountType())
                    .customSql(dto.getCustomSql())
                    .tenantId(dto.getTenantId())
                    .build();
            tableLineDTOList.add(tableLineDTO);
        }
        //插入规则行，获取返回主键
        List<BatchPlanTableLineDTO> lineList = batchPlanTableLineRepository.batchInsertDTOSelective(tableLineDTOList);
        List<BatchPlanTableConDTO> tableConDTOList=new ArrayList<>();
        for(int i = 0; i < batchPlanTableDTOS.size(); i++) {
            BatchPlanTableDTO dto = batchPlanTableDTOS.get(i);
            BatchPlanTableConDTO tableConDTO = BatchPlanTableConDTO.builder()
                    .planLineId(lineList.get(i).getPlanLineId())
                    .whereCondition(dto.getWhereCondition())
                    .compareWay(dto.getCompareWay())
                    .warningLevel(dto.getWarningLevel())
                    .tenantId(dto.getTenantId())
                    .build();
            tableConDTOList.add(tableConDTO);
        }
        //插入告警配置
        batchPlanTableConRepository.batchInsertDTOSelective(tableConDTOList);
        return true;
    }
}
