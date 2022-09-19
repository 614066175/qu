package com.hand.hdsp.quality.infra.batchimport;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hand.hdsp.core.util.ProjectHelper;
import com.hand.hdsp.quality.api.dto.BatchPlanBaseDTO;
import com.hand.hdsp.quality.api.dto.BatchPlanTableConDTO;
import com.hand.hdsp.quality.api.dto.BatchPlanTableDTO;
import com.hand.hdsp.quality.api.dto.BatchPlanTableLineDTO;
import com.hand.hdsp.quality.domain.entity.BatchPlanBase;
import com.hand.hdsp.quality.domain.entity.BatchPlanTable;
import com.hand.hdsp.quality.domain.entity.BatchPlanTableCon;
import com.hand.hdsp.quality.domain.entity.BatchPlanTableLine;
import com.hand.hdsp.quality.domain.repository.BatchPlanBaseRepository;
import com.hand.hdsp.quality.domain.repository.BatchPlanTableConRepository;
import com.hand.hdsp.quality.domain.repository.BatchPlanTableLineRepository;
import com.hand.hdsp.quality.domain.repository.BatchPlanTableRepository;
import com.hand.hdsp.quality.infra.constant.TemplateCodeConstants;
import com.hand.hdsp.quality.infra.converter.BatchPlanTableConConverter;
import com.hand.hdsp.quality.infra.converter.BatchPlanTableLineConverter;
import io.choerodon.core.oauth.DetailsHelper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.hzero.boot.imported.app.service.BatchImportHandler;
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
 * @author lgl 2021/03/11 15:14
 * @since 1.0
 */
@Slf4j
@ImportService(templateCode = TemplateCodeConstants.TEMPLATE_CODE_BATCH_PLAN, sheetIndex = 3)
public class PlanTableBatchImportImpl extends BatchImportHandler implements IBatchImportService {

    private final ObjectMapper objectMapper;

    private final BatchPlanTableRepository batchPlanTableRepository;

    private final BatchPlanTableLineRepository batchPlanTableLineRepository;

    private final BatchPlanTableConRepository batchPlanTableConRepository;

    private final BatchPlanBaseRepository batchPlanBaseRepository;

    private final BatchPlanTableLineConverter batchPlanTableLineConverter;

    private final BatchPlanTableConConverter batchPlanTableConConverter;

    public PlanTableBatchImportImpl(ObjectMapper objectMapper, BatchPlanTableRepository batchPlanTableRepository, BatchPlanTableLineRepository batchPlanTableLineRepository, BatchPlanTableConRepository batchPlanTableConRepository, BatchPlanBaseRepository batchPlanBaseRepository, BatchPlanTableLineConverter batchPlanTableLineConverter, BatchPlanTableConConverter batchPlanTableConConverter) {
        this.objectMapper = objectMapper;
        this.batchPlanTableRepository = batchPlanTableRepository;
        this.batchPlanTableLineRepository = batchPlanTableLineRepository;
        this.batchPlanTableConRepository = batchPlanTableConRepository;
        this.batchPlanBaseRepository = batchPlanBaseRepository;
        this.batchPlanTableLineConverter = batchPlanTableLineConverter;
        this.batchPlanTableConConverter = batchPlanTableConConverter;
    }

    @Override
    public Boolean doImport(List<String> data) {
        // 设置租户Id
        Long tenantId = DetailsHelper.getUserDetails().getTenantId();
        Long projectId = ProjectHelper.getProjectId();
        List<BatchPlanTableDTO> batchPlanTableDTOS = new ArrayList<>(data.size());
        try {
            int index = 0;
            for (String json : data) {
                BatchPlanTableDTO batchPlanTableDTO = objectMapper.readValue(json, BatchPlanTableDTO.class);
                batchPlanTableDTO.setExceptionBlock("是".equals(batchPlanTableDTO.getExceptionBlockFlag()) ? 1 : 0);
                batchPlanTableDTO.setTenantId(tenantId);
                batchPlanTableDTO.setProjectId(projectId);
                //查询planBaseId
                List<BatchPlanBaseDTO> batchPlanBaseDTOS = batchPlanBaseRepository.selectDTOByCondition(Condition.builder(BatchPlanBase.class)
                        .andWhere(Sqls.custom()
                                .andEqualTo(BatchPlanBase.FIELD_PLAN_BASE_CODE, batchPlanTableDTO.getPlanBaseCode())
                                .andEqualTo(BatchPlanBase.FIELD_TENANT_ID, batchPlanTableDTO.getTenantId())
                                .andEqualTo(BatchPlanBase.FIELD_PROJECT_ID, batchPlanTableDTO.getProjectId()))
                        .build());
                if (CollectionUtils.isNotEmpty(batchPlanBaseDTOS)) {
                    batchPlanTableDTO.setPlanBaseId(batchPlanBaseDTOS.get(0).getPlanBaseId());
                } else {
                    addErrorMsg(index, String.format("质检项编码【%s】不存在", batchPlanTableDTO.getPlanBaseCode()));
                    return false;
                }
                batchPlanTableDTOS.add(batchPlanTableDTO);
                index++;
            }
        } catch (IOException e) {
            // 失败
            log.error("Permission Object data:{}", data);
            log.error("Permission Object Read Json Error", e);
            return false;
        }
        //看表级规则存不存在
        List<BatchPlanTableDTO> tableList = new ArrayList<>();
        batchPlanTableDTOS.forEach(batchPlanTableDTO -> {
            BatchPlanTable batchPlanTable = batchPlanTableRepository.selectOne(BatchPlanTable.builder().planBaseId(batchPlanTableDTO.getPlanBaseId())
                    .ruleCode(batchPlanTableDTO.getRuleCode())
                    .build());
            if (batchPlanTable != null) {
                //已存在则更新
                batchPlanTableDTO.setPlanRuleId(batchPlanTable.getPlanRuleId());
                batchPlanTableDTO.setObjectVersionNumber(batchPlanTable.getObjectVersionNumber());
                batchPlanTableRepository.updateByDTOPrimaryKeySelective(batchPlanTableDTO);
                tableList.add(batchPlanTableDTO);
            } else {
                batchPlanTableRepository.insertDTOSelective(batchPlanTableDTO);
                tableList.add(batchPlanTableDTO);
            }
        });

//        //插入表级规则,获取返回主键
//        List<BatchPlanTableDTO> tableList = batchPlanTableRepository.batchInsertDTOSelective(batchPlanTableDTOS);
        //插入规则行
        List<BatchPlanTableLineDTO> tableLineDTOList = new ArrayList<>();
        for (int i = 0; i < batchPlanTableDTOS.size(); i++) {
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

        List<BatchPlanTableLineDTO> lineList = new ArrayList<>();
        tableLineDTOList.forEach(batchPlanTableLineDTO -> {
            BatchPlanTableLine batchPlanTableLine = batchPlanTableLineRepository.selectOne(batchPlanTableLineConverter.dtoToEntity(batchPlanTableLineDTO));
            if (batchPlanTableLine != null) {
                //已存在则更新
                batchPlanTableLineDTO.setPlanLineId(batchPlanTableLine.getPlanLineId());
                batchPlanTableLineDTO.setObjectVersionNumber(batchPlanTableLine.getObjectVersionNumber());
                batchPlanTableLineRepository.updateByDTOPrimaryKeySelective(batchPlanTableLineDTO);
                lineList.add(batchPlanTableLineDTO);
            } else {
                batchPlanTableLineRepository.insertDTOSelective(batchPlanTableLineDTO);
                lineList.add(batchPlanTableLineDTO);
            }
        });
//        //插入规则行，获取返回主键
//        List<BatchPlanTableLineDTO> lineList = batchPlanTableLineRepository.batchInsertDTOSelective(tableLineDTOList);
        List<BatchPlanTableConDTO> tableConDTOList = new ArrayList<>();
        for (int i = 0; i < batchPlanTableDTOS.size(); i++) {
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

        tableConDTOList.forEach(tableConDTO -> {
            BatchPlanTableCon batchPlanTableCon = batchPlanTableConRepository.selectOne(batchPlanTableConConverter.dtoToEntity(tableConDTO));
            if (batchPlanTableCon != null) {
                //已存在则更新
                tableConDTO.setConditionId(batchPlanTableCon.getConditionId());
                tableConDTO.setObjectVersionNumber(batchPlanTableCon.getObjectVersionNumber());
                batchPlanTableConRepository.updateByDTOPrimaryKeySelective(tableConDTO);
            } else {
                batchPlanTableConRepository.insertDTOSelective(tableConDTO);
            }
        });
//        //插入告警配置
//        batchPlanTableConRepository.batchInsertDTOSelective(tableConDTOList);
        return true;
    }
}
