package com.hand.hdsp.quality.app.service.impl;

import com.hand.hdsp.quality.api.dto.BatchPlanFieldConDTO;
import com.hand.hdsp.quality.api.dto.BatchPlanFieldDTO;
import com.hand.hdsp.quality.api.dto.BatchPlanFieldLineDTO;
import com.hand.hdsp.quality.app.service.BatchPlanFieldService;
import com.hand.hdsp.quality.domain.entity.BatchPlanFieldCon;
import com.hand.hdsp.quality.domain.entity.BatchPlanFieldLine;
import com.hand.hdsp.quality.domain.repository.BatchPlanFieldConRepository;
import com.hand.hdsp.quality.domain.repository.BatchPlanFieldLineRepository;
import com.hand.hdsp.quality.domain.repository.BatchPlanFieldRepository;
import com.hand.hdsp.quality.domain.repository.RuleRepository;
import com.hand.hdsp.quality.infra.util.JsonUtils;
import io.choerodon.mybatis.domain.AuditDomain;
import org.apache.commons.collections4.CollectionUtils;
import org.hzero.core.base.BaseConstants;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>批数据方案-字段规则表应用服务默认实现</p>
 *
 * @author feng.liu01@hand-china.com 2020-03-24 16:19:53
 */
@Service
public class BatchPlanFieldServiceImpl implements BatchPlanFieldService {

    private final BatchPlanFieldRepository batchPlanFieldRepository;
    private final BatchPlanFieldLineRepository batchPlanFieldLineRepository;
    private final BatchPlanFieldConRepository batchPlanFieldConRepository;
    private final RuleRepository ruleRepository;

    public BatchPlanFieldServiceImpl(BatchPlanFieldRepository batchPlanFieldRepository,
                                     BatchPlanFieldLineRepository batchPlanFieldLineRepository,
                                     RuleRepository ruleRepository,
                                     BatchPlanFieldConRepository batchPlanFieldConRepository) {
        this.batchPlanFieldRepository = batchPlanFieldRepository;
        this.batchPlanFieldLineRepository = batchPlanFieldLineRepository;
        this.ruleRepository = ruleRepository;
        this.batchPlanFieldConRepository = batchPlanFieldConRepository;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int delete(BatchPlanFieldDTO batchPlanFieldDTO) {
        List<BatchPlanFieldLineDTO> batchPlanFieldLineDTOList =
                batchPlanFieldLineRepository.selectDTO(
                        BatchPlanFieldLine.FIELD_PLAN_RULE_ID, batchPlanFieldDTO.getPlanRuleId());
        if (batchPlanFieldLineDTOList != null) {
            batchPlanFieldLineRepository.deleteByParentId(batchPlanFieldDTO.getPlanRuleId());
        }
        return batchPlanFieldRepository.deleteByPrimaryKey(batchPlanFieldDTO);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void insert(BatchPlanFieldDTO batchPlanFieldDTO) {
        Long tenantId = batchPlanFieldDTO.getTenantId();
        batchPlanFieldRepository.insertDTOSelective(batchPlanFieldDTO);
        if (batchPlanFieldDTO.getBatchPlanFieldLineDTOList() != null) {

            for (BatchPlanFieldLineDTO batchPlanFieldLineDTO : batchPlanFieldDTO.getBatchPlanFieldLineDTOList()) {
                batchPlanFieldLineDTO.setPlanRuleId(batchPlanFieldDTO.getPlanRuleId());
                batchPlanFieldLineDTO.setTenantId(tenantId);
                batchPlanFieldLineRepository.insertDTOSelective(batchPlanFieldLineDTO);

                if (CollectionUtils.isNotEmpty(batchPlanFieldLineDTO.getBatchPlanFieldConDTOList())) {
                    for (BatchPlanFieldConDTO batchPlanFieldConDTO : batchPlanFieldLineDTO.getBatchPlanFieldConDTOList()) {
                        batchPlanFieldConDTO.setPlanLineId(batchPlanFieldLineDTO.getPlanLineId());
                        batchPlanFieldConDTO.setTenantId(tenantId);
                        batchPlanFieldConDTO.setWarningLevel(JsonUtils.object2Json(batchPlanFieldConDTO.getWarningLevelDTOList()));
                        batchPlanFieldConRepository.insertDTOSelective(batchPlanFieldConDTO);
                    }
                }
            }
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(BatchPlanFieldDTO batchPlanFieldDTO) {
        Long tenantId = batchPlanFieldDTO.getTenantId();
        batchPlanFieldRepository.updateDTOWhereTenant(batchPlanFieldDTO, tenantId);
        if (batchPlanFieldDTO.getBatchPlanFieldLineDTOList() != null) {
            for (BatchPlanFieldLineDTO batchPlanFieldLineDTO : batchPlanFieldDTO.getBatchPlanFieldLineDTOList()) {
                if (AuditDomain.RecordStatus.update.equals(batchPlanFieldLineDTO.get_status())) {
                    batchPlanFieldLineRepository.updateDTOWhereTenant(batchPlanFieldLineDTO, tenantId);
                } else if (AuditDomain.RecordStatus.create.equals(batchPlanFieldLineDTO.get_status())) {
                    batchPlanFieldLineDTO.setPlanRuleId(batchPlanFieldDTO.getPlanRuleId());
                    batchPlanFieldLineDTO.setTenantId(tenantId);
                    batchPlanFieldLineRepository.insertDTOSelective(batchPlanFieldLineDTO);
                } else if (AuditDomain.RecordStatus.delete.equals(batchPlanFieldLineDTO.get_status())) {
                    batchPlanFieldLineRepository.deleteByPrimaryKey(batchPlanFieldLineDTO);
                }
                if (CollectionUtils.isNotEmpty(batchPlanFieldLineDTO.getBatchPlanFieldConDTOList())) {
                    for (BatchPlanFieldConDTO con : batchPlanFieldLineDTO.getBatchPlanFieldConDTOList()) {
                        if (AuditDomain.RecordStatus.update.equals(con.get_status())) {
                            con.setWarningLevel(JsonUtils.object2Json(con.getWarningLevelDTOList()));
                            batchPlanFieldConRepository.updateDTOWhereTenant(con, tenantId);
                        } else if (AuditDomain.RecordStatus.create.equals(con.get_status())) {
                            con.setWarningLevel(JsonUtils.object2Json(con.getWarningLevelDTOList()));
                            con.setPlanLineId(batchPlanFieldLineDTO.getPlanLineId());
                            con.setTenantId(tenantId);
                            batchPlanFieldConRepository.insertDTOSelective(con);
                        } else if (AuditDomain.RecordStatus.delete.equals(con.get_status())) {
                            batchPlanFieldConRepository.deleteByPrimaryKey(con);
                        }
                    }
                }
            }
        }
    }

    @Override
    public BatchPlanFieldDTO detail(Long planRuleId) {
        BatchPlanFieldDTO batchPlanFieldDTO = batchPlanFieldRepository.selectDTOByPrimaryKey(planRuleId);
        List<BatchPlanFieldLineDTO> batchPlanFieldLineDTOList =
                batchPlanFieldLineRepository.selectDTO(BatchPlanFieldLine.FIELD_PLAN_RULE_ID, planRuleId);
        for (BatchPlanFieldLineDTO batchPlanFieldLineDTO : batchPlanFieldLineDTOList) {
            List<BatchPlanFieldConDTO> conDTOList = batchPlanFieldConRepository.selectDTO(BatchPlanFieldCon.FIELD_PLAN_LINE_ID, batchPlanFieldLineDTO.getPlanLineId());
            conDTOList.forEach(dto -> dto.setWarningLevelDTOList(JsonUtils.json2WarningLevel(dto.getWarningLevel())));
            batchPlanFieldLineDTO.setBatchPlanFieldConDTOList(conDTOList);
        }
        batchPlanFieldDTO.setBatchPlanFieldLineDTOList(batchPlanFieldLineDTOList);
        return batchPlanFieldDTO;
    }

    @Override
    public Map<String, List<BatchPlanFieldDTO>> listSelected(BatchPlanFieldDTO batchPlanFieldDTO) {
        List<BatchPlanFieldDTO> list = batchPlanFieldRepository.selectList(batchPlanFieldDTO);
        return list.stream().map(rule -> {
            //如果包含逗号，则按逗号分隔
            if (rule.getFieldName().contains(BaseConstants.Symbol.COMMA)) {
                return Arrays.stream(rule.getFieldName().split(BaseConstants.Symbol.COMMA)).map(s -> {
                    BatchPlanFieldDTO dto = BatchPlanFieldDTO.builder().build();
                    BeanUtils.copyProperties(rule, dto);
                    dto.setFieldName(s);
                    return dto;
                }).collect(Collectors.toList());
            }
            return Collections.singletonList(rule);
        })
                //将list拉平
                .flatMap(Collection::stream)
                .distinct()
                .collect(Collectors.groupingBy(BatchPlanFieldDTO::getFieldName));
    }

    @Override
    public BatchPlanFieldDTO selectDetail(Long ruleId) {
        // todo
        return null;
    }
}
