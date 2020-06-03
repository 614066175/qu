package com.hand.hdsp.quality.app.service.impl;

import com.hand.hdsp.quality.api.dto.BatchPlanFieldDTO;
import com.hand.hdsp.quality.api.dto.BatchPlanFieldLineDTO;
import com.hand.hdsp.quality.api.dto.PlanWarningLevelDTO;
import com.hand.hdsp.quality.api.dto.RuleDTO;
import com.hand.hdsp.quality.app.service.BatchPlanFieldService;
import com.hand.hdsp.quality.domain.entity.BatchPlanField;
import com.hand.hdsp.quality.domain.entity.BatchPlanFieldLine;
import com.hand.hdsp.quality.domain.entity.PlanWarningLevel;
import com.hand.hdsp.quality.domain.repository.BatchPlanFieldLineRepository;
import com.hand.hdsp.quality.domain.repository.BatchPlanFieldRepository;
import com.hand.hdsp.quality.domain.repository.PlanWarningLevelRepository;
import com.hand.hdsp.quality.domain.repository.RuleRepository;
import com.hand.hdsp.quality.infra.constant.ErrorCode;
import com.hand.hdsp.quality.infra.constant.TableNameConstant;
import com.hand.hdsp.quality.infra.converter.BatchPlanFieldConverter;
import com.hand.hdsp.quality.infra.converter.PlanWarningLevelConverter;
import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.domain.AuditDomain;
import io.choerodon.mybatis.pagehelper.PageHelper;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>批数据方案-字段规则表应用服务默认实现</p>
 *
 * @author feng.liu01@hand-china.com 2020-03-24 16:19:53
 */
@Service
public class BatchPlanFieldServiceImpl implements BatchPlanFieldService {

    private final BatchPlanFieldRepository batchPlanFieldRepository;
    private final BatchPlanFieldLineRepository batchPlanFieldLineRepository;
    private final PlanWarningLevelRepository planWarningLevelRepository;
    private final PlanWarningLevelConverter planWarningLevelConverter;
    private final BatchPlanFieldConverter batchPlanFieldConverter;
    private final RuleRepository ruleRepository;

    public BatchPlanFieldServiceImpl(BatchPlanFieldRepository batchPlanFieldRepository,
                                     BatchPlanFieldLineRepository batchPlanFieldLineRepository,
                                     PlanWarningLevelRepository planWarningLevelRepository,
                                     PlanWarningLevelConverter planWarningLevelConverter,
                                     BatchPlanFieldConverter batchPlanFieldConverter,
                                     RuleRepository ruleRepository) {
        this.batchPlanFieldRepository = batchPlanFieldRepository;
        this.batchPlanFieldLineRepository = batchPlanFieldLineRepository;
        this.planWarningLevelRepository = planWarningLevelRepository;
        this.planWarningLevelConverter = planWarningLevelConverter;
        this.batchPlanFieldConverter = batchPlanFieldConverter;
        this.ruleRepository = ruleRepository;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int delete(BatchPlanFieldDTO batchPlanFieldDTO) {
        List<BatchPlanFieldLineDTO> batchPlanFieldLineDTOList =
                batchPlanFieldLineRepository.selectDTO(
                        BatchPlanFieldLine.FIELD_PLAN_FIELD_ID, batchPlanFieldDTO.getPlanFieldId());
        if (batchPlanFieldLineDTOList != null) {
            for (BatchPlanFieldLineDTO batchPlanFieldLineDTO : batchPlanFieldLineDTOList) {
                planWarningLevelRepository.deleteByParentId(batchPlanFieldLineDTO.getPlanFieldLineId(),
                        TableNameConstant.XQUA_BATCH_PLAN_FIELD_LINE);
            }
            batchPlanFieldLineRepository.deleteByParentId(batchPlanFieldDTO.getPlanFieldId());
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
                batchPlanFieldLineDTO.setPlanFieldId(batchPlanFieldDTO.getPlanFieldId());
                batchPlanFieldLineDTO.setTenantId(tenantId);
                batchPlanFieldLineRepository.insertDTOSelective(batchPlanFieldLineDTO);

                if (batchPlanFieldLineDTO.getPlanWarningLevelDTOList() != null) {
                    for (PlanWarningLevelDTO planWarningLevelDTO : batchPlanFieldLineDTO.getPlanWarningLevelDTOList()) {
                        planWarningLevelDTO.setSourceId(batchPlanFieldLineDTO.getPlanFieldLineId());
                        planWarningLevelDTO.setSourceType(TableNameConstant.XQUA_BATCH_PLAN_FIELD_LINE);
                        planWarningLevelDTO.setTenantId(tenantId);
                        Assert.notNull(planWarningLevelRepository.judgeOverlap(planWarningLevelDTO), ErrorCode.WARNING_LEVEL_OVERLAP);
                        planWarningLevelRepository.insertDTOSelective(planWarningLevelDTO);
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
                    batchPlanFieldLineDTO.setPlanFieldId(batchPlanFieldDTO.getPlanFieldId());
                    batchPlanFieldLineDTO.setTenantId(tenantId);
                    batchPlanFieldLineRepository.insertDTOSelective(batchPlanFieldLineDTO);
                } else if (AuditDomain.RecordStatus.delete.equals(batchPlanFieldLineDTO.get_status())) {
                    batchPlanFieldLineRepository.deleteByPrimaryKey(batchPlanFieldLineDTO);
                }
                if (batchPlanFieldLineDTO.getPlanWarningLevelDTOList() != null) {
                    for (PlanWarningLevelDTO planWarningLevelDTO : batchPlanFieldLineDTO.getPlanWarningLevelDTOList()) {
                        if (AuditDomain.RecordStatus.update.equals(planWarningLevelDTO.get_status())) {
                            Assert.notNull(planWarningLevelRepository.judgeOverlap(planWarningLevelDTO), ErrorCode.WARNING_LEVEL_OVERLAP);
                            planWarningLevelRepository.updateDTOWhereTenant(planWarningLevelDTO, tenantId);
                        } else if (AuditDomain.RecordStatus.create.equals(planWarningLevelDTO.get_status())) {
                            planWarningLevelDTO.setSourceId(batchPlanFieldLineDTO.getPlanFieldLineId());
                            planWarningLevelDTO.setSourceType(TableNameConstant.XQUA_BATCH_PLAN_FIELD_LINE);
                            planWarningLevelDTO.setTenantId(tenantId);
                            Assert.notNull(planWarningLevelRepository.judgeOverlap(planWarningLevelDTO), ErrorCode.WARNING_LEVEL_OVERLAP);
                            planWarningLevelRepository.insertDTOSelective(planWarningLevelDTO);
                        } else if (AuditDomain.RecordStatus.delete.equals(planWarningLevelDTO.get_status())) {
                            planWarningLevelRepository.deleteByPrimaryKey(planWarningLevelDTO);
                        }
                    }
                }
            }
        }
    }

    @Override
    public BatchPlanFieldDTO detail(Long planFieldId) {
        BatchPlanFieldDTO batchPlanFieldDTO = batchPlanFieldRepository.selectDTOByPrimaryKey(planFieldId);
        List<BatchPlanFieldLineDTO> batchPlanFieldLineDTOList =
                batchPlanFieldLineRepository.selectDTO(BatchPlanFieldLine.FIELD_PLAN_FIELD_ID, planFieldId);
        for (BatchPlanFieldLineDTO batchPlanFieldLineDTO : batchPlanFieldLineDTOList) {
            List<PlanWarningLevel> planWarningLevelList = planWarningLevelRepository.select(
                    PlanWarningLevel.builder()
                            .sourceId(batchPlanFieldLineDTO.getPlanFieldLineId())
                            .sourceType(TableNameConstant.XQUA_BATCH_PLAN_FIELD_LINE).build());
            List<PlanWarningLevelDTO> planWarningLevelDTOList = new ArrayList<>();
            for (PlanWarningLevel planWarningLevel : planWarningLevelList) {
                planWarningLevelDTOList.add(planWarningLevelConverter.entityToDto(planWarningLevel));
            }
            batchPlanFieldLineDTO.setPlanWarningLevelDTOList(planWarningLevelDTOList);
        }
        batchPlanFieldDTO.setBatchPlanFieldLineDTOList(batchPlanFieldLineDTOList);
        return batchPlanFieldDTO;
    }

    @Override
    public Page<BatchPlanFieldDTO> list(PageRequest pageRequest, BatchPlanFieldDTO batchPlanFieldDTO) {
        Page<BatchPlanFieldDTO> list = batchPlanFieldRepository.distinctPageAndSortDTO(pageRequest, batchPlanFieldDTO);
        for (BatchPlanFieldDTO batchPlanFieldDTO1 : list) {
            List<BatchPlanFieldDTO> batchPlanFieldDTOList = new ArrayList<>();
            List<BatchPlanField> batchPlanFieldList = batchPlanFieldRepository.select(BatchPlanField.builder()
                    .planBaseId(batchPlanFieldDTO1.getPlanBaseId())
                    .fieldName(batchPlanFieldDTO1.getFieldName()).build());
            for (BatchPlanField batchPlanField : batchPlanFieldList) {
                batchPlanFieldDTOList.add(batchPlanFieldConverter.entityToDto(batchPlanField));
            }
            batchPlanFieldDTO1.setBatchPlanFieldDTOList(batchPlanFieldDTOList);
        }
        return list;
    }

    @Override
    public Page<RuleDTO> select(BatchPlanFieldDTO batchPlanFieldDTO, String ruleModel, PageRequest pageRequest) {
        Long tenantId = batchPlanFieldDTO.getTenantId();
        List<BatchPlanField> batchPlanFieldList = batchPlanFieldRepository.list(BatchPlanField.builder()
                .planBaseId(batchPlanFieldDTO.getPlanBaseId())
                .fieldName(batchPlanFieldDTO.getFieldName())
                .ruleName(batchPlanFieldDTO.getRuleName())
                .tenantId(tenantId).build());
        List<String> ruleCodeList = new ArrayList<>();
        RuleDTO ruleDTO2 = new RuleDTO();
        ruleDTO2.setRuleName(batchPlanFieldDTO.getRuleName());
        ruleDTO2.setTenantId(tenantId);
        Page<RuleDTO> ruleDTOListAll = PageHelper.doPage(pageRequest, () -> ruleRepository.listAll(ruleDTO2));
        if (!batchPlanFieldList.isEmpty()) {
            for (BatchPlanField batchPlanField : batchPlanFieldList) {
                ruleCodeList.add(batchPlanField.getRuleCode());
            }
            List<RuleDTO> ruleDTOList = ruleRepository.list(ruleCodeList, ruleModel, tenantId);
            if (ruleDTOList != null) {
                for (RuleDTO ruleDTO : ruleDTOList) {
                    for (RuleDTO ruleDTO1 : ruleDTOListAll) {
                        if (ruleDTO1.getRuleId().equals(ruleDTO.getRuleId())) {
                            ruleDTO1.setSelectedFlag(1);
                        }
                    }
                }
            }
        } else {
            return ruleDTOListAll;
        }
        return ruleDTOListAll;
    }
}
