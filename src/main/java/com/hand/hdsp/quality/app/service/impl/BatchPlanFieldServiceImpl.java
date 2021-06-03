package com.hand.hdsp.quality.app.service.impl;

import static com.hand.hdsp.quality.infra.constant.PlanConstant.CheckType.STANDARD;
import static com.hand.hdsp.quality.infra.constant.StandardConstant.AimType.REFERENCE;
import static com.hand.hdsp.quality.infra.constant.StandardConstant.StandardType.DATA;

import java.util.*;
import java.util.stream.Collectors;

import com.hand.hdsp.quality.api.dto.*;
import com.hand.hdsp.quality.app.service.BatchPlanFieldService;
import com.hand.hdsp.quality.domain.entity.*;
import com.hand.hdsp.quality.domain.repository.*;
import com.hand.hdsp.quality.infra.constant.ErrorCode;
import com.hand.hdsp.quality.infra.util.JsonUtils;
import io.choerodon.core.domain.Page;
import io.choerodon.core.exception.CommonException;
import io.choerodon.mybatis.domain.AuditDomain;
import io.choerodon.mybatis.pagehelper.PageHelper;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import org.apache.commons.collections4.CollectionUtils;
import org.hzero.boot.driver.app.service.DriverSessionService;
import org.hzero.core.base.BaseConstants;
import org.hzero.mybatis.domian.Condition;
import org.hzero.mybatis.util.Sqls;
import org.hzero.starter.driver.core.infra.meta.Column;
import org.hzero.starter.driver.core.infra.meta.Table;
import org.hzero.starter.driver.core.session.DriverSession;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    private final RuleLineRepository ruleLineRepository;
    private final DataStandardRepository dataStandardRepository;
    private final BatchPlanBaseRepository batchPlanBaseRepository;
    private final DriverSessionService driverSessionService;
    private final StandardAimRepository standardAimRepository;
    private final StandardAimRelationRepository standardAimRelationRepository;

    public BatchPlanFieldServiceImpl(BatchPlanFieldRepository batchPlanFieldRepository,
                                     BatchPlanFieldLineRepository batchPlanFieldLineRepository,
                                     RuleRepository ruleRepository,
                                     BatchPlanFieldConRepository batchPlanFieldConRepository,
                                     RuleLineRepository ruleLineRepository,
                                     DataStandardRepository dataStandardRepository,
                                     BatchPlanBaseRepository batchPlanBaseRepository,
                                     DriverSessionService driverSessionService,
                                     StandardAimRepository standardAimRepository,
                                     StandardAimRelationRepository standardAimRelationRepository) {
        this.batchPlanFieldRepository = batchPlanFieldRepository;
        this.batchPlanFieldLineRepository = batchPlanFieldLineRepository;
        this.ruleRepository = ruleRepository;
        this.batchPlanFieldConRepository = batchPlanFieldConRepository;
        this.ruleLineRepository = ruleLineRepository;
        this.dataStandardRepository = dataStandardRepository;
        this.batchPlanBaseRepository = batchPlanBaseRepository;
        this.driverSessionService = driverSessionService;
        this.standardAimRepository = standardAimRepository;
        this.standardAimRelationRepository = standardAimRelationRepository;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(BatchPlanFieldDTO batchPlanFieldDTO) {
        batchPlanFieldDTO = batchPlanFieldRepository.selectDTOByPrimaryKey(batchPlanFieldDTO.getPlanRuleId());
        if (Objects.isNull(batchPlanFieldDTO)) {
            throw new CommonException(ErrorCode.BATCH_PLAN_FIELD_NOT_EXIST);
        }
        BatchPlanBaseDTO batchPlanBaseDTO = batchPlanBaseRepository.selectDTOByPrimaryKey(batchPlanFieldDTO.getPlanBaseId());
        if (Objects.isNull(batchPlanBaseDTO)) {
            throw new CommonException(ErrorCode.BATCH_PLAN_BASE_NOT_EXIST);
        }
        List<BatchPlanFieldLineDTO> batchPlanFieldLineDTOList =
                batchPlanFieldLineRepository.selectDTO(
                        BatchPlanFieldLine.FIELD_PLAN_RULE_ID, batchPlanFieldDTO.getPlanRuleId());
        //删除行表
        if (CollectionUtils.isNotEmpty(batchPlanFieldLineDTOList)) {
            batchPlanFieldLineRepository.deleteByParentId(batchPlanFieldDTO.getPlanRuleId());
        }
        //删除字段规则表
        batchPlanFieldRepository.deleteByPrimaryKey(batchPlanFieldDTO);
        //判断字段规则是不是数据标准，是的话，就从数据标准中移除落标
        if (STANDARD.equals(batchPlanFieldDTO.getCheckType())) {
            //查询数据标准中是否有此规则
            List<DataStandardDTO> dataStandardDTOList = dataStandardRepository.selectDTOByCondition(Condition.builder(DataStandard.class)
                    .andWhere(Sqls.custom()
                            .andEqualTo(DataStandard.FIELD_STANDARD_CODE, batchPlanFieldDTO.getRuleCode())
                            .andEqualTo(DataStandard.FIELD_TENANT_ID, batchPlanFieldDTO.getTenantId()))
                    .build());
            if (CollectionUtils.isNotEmpty(dataStandardDTOList)
                    && CollectionUtils.isNotEmpty(batchPlanFieldLineDTOList)) {
                DataStandardDTO dataStandardDTO = dataStandardDTOList.get(0);
                List<StandardAimDTO> standardAimDTOList = new ArrayList<>();
                List<StandardAimRelationDTO> standardAimRelationDTOList = new ArrayList<>();
                batchPlanFieldLineDTOList.forEach(batchPlanFieldLineDTO -> {
                    List<StandardAimDTO> aimList = standardAimRepository.selectDTOByCondition(Condition.builder(StandardAim.class)
                            .andWhere(Sqls.custom()
                                    .andEqualTo(StandardAim.FIELD_STANDARD_ID, dataStandardDTO.getStandardId())
                                    .andEqualTo(StandardAim.FIELD_STANDARD_TYPE, DATA)
                                    .andEqualTo(StandardAim.FIELD_FIELD_NAME, batchPlanFieldLineDTO.getFieldName())
                                    .andEqualTo(StandardAim.FIELD_DATASOURCE_ID, batchPlanBaseDTO.getDatasourceId())
                                    .andEqualTo(StandardAim.FIELD_TABLE_NAME, batchPlanBaseDTO.getObjectName())
                                    .andEqualTo(StandardAim.FIELD_TENANT_ID, batchPlanBaseDTO.getTenantId()))
                            .build());
                    standardAimDTOList.addAll(aimList);
                    aimList.forEach(standardAimDTO -> {
                        List<StandardAimRelationDTO> relationList = standardAimRelationRepository.selectDTOByCondition(Condition.builder(StandardAimRelation.class)
                                .andWhere(Sqls.custom()
                                        .andEqualTo(StandardAimRelation.FIELD_AIM_ID, standardAimDTO.getAimId())
                                        .andEqualTo(StandardAimRelation.FIELD_TENANT_ID, standardAimDTO.getTenantId()))
                                .build());
                        standardAimRelationDTOList.addAll(relationList);
                    });
                });
                //批量删除落标表和落标关系表
                standardAimRepository.batchDTODeleteByPrimaryKey(standardAimDTOList);
                standardAimRelationRepository.batchDTODeleteByPrimaryKey(standardAimRelationDTOList);
            }
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void insert(BatchPlanFieldDTO batchPlanFieldDTO) {
        Long tenantId = batchPlanFieldDTO.getTenantId();
        batchPlanFieldRepository.insertDTOSelective(batchPlanFieldDTO);
        List<BatchPlanFieldLineDTO> batchPlanFieldLineDTOList = batchPlanFieldDTO.getBatchPlanFieldLineDTOList();
        if (CollectionUtils.isNotEmpty(batchPlanFieldLineDTOList)) {

            for (BatchPlanFieldLineDTO batchPlanFieldLineDTO : batchPlanFieldLineDTOList) {
                batchPlanFieldLineDTO.setPlanRuleId(batchPlanFieldDTO.getPlanRuleId());
                batchPlanFieldLineDTO.setTenantId(tenantId);
                batchPlanFieldLineRepository.insertDTOSelective(batchPlanFieldLineDTO);

                if (CollectionUtils.isNotEmpty(batchPlanFieldLineDTO.getBatchPlanFieldConDTOList())) {
                    for (BatchPlanFieldConDTO batchPlanFieldConDTO : batchPlanFieldLineDTO.getBatchPlanFieldConDTOList()) {
                        batchPlanFieldConDTO.setPlanLineId(batchPlanFieldLineDTO.getPlanLineId());
                        batchPlanFieldConDTO.setTenantId(tenantId);
                        batchPlanFieldConDTO.setWarningLevel(JsonUtils.object2Json(batchPlanFieldConDTO.getWarningLevelList()));
                        batchPlanFieldConRepository.insertDTOSelective(batchPlanFieldConDTO);
                    }
                }
            }
        }
        //如果标准为规范性
        if (STANDARD.equals(batchPlanFieldDTO.getCheckType())) {
            //查询数据标准中是否有此规则
            List<DataStandardDTO> dataStandardDTOList = dataStandardRepository.selectDTOByCondition(Condition.builder(DataStandard.class)
                    .andWhere(Sqls.custom()
                            .andEqualTo(DataStandard.FIELD_STANDARD_CODE, batchPlanFieldDTO.getRuleCode())
                            .andEqualTo(DataStandard.FIELD_TENANT_ID, batchPlanFieldDTO.getTenantId()))
                    .build());
            //规则为数据标准且规则校验项不为空
            if (CollectionUtils.isNotEmpty(dataStandardDTOList) && CollectionUtils.isNotEmpty(batchPlanFieldLineDTOList)) {
                //查询基础配置
                BatchPlanBaseDTO batchPlanBaseDTO = batchPlanBaseRepository.selectDTOByPrimaryKey(batchPlanFieldDTO.getPlanBaseId());
                DataStandardDTO dataStandardDTO = dataStandardDTOList.get(0);
                batchPlanFieldLineDTOList.forEach(batchPlanFieldLineDTO -> {
                    //在数据标准中落标此字段
                    StandardAimDTO standardAimDTO = StandardAimDTO.builder()
                            .standardId(dataStandardDTO.getStandardId())
                            .standardType(DATA)
                            .fieldName(batchPlanFieldLineDTO.getFieldName())
                            .datasourceId(batchPlanBaseDTO.getDatasourceId())
                            .datasourceType(batchPlanBaseDTO.getDatasourceType())
                            .datasourceCode(batchPlanBaseDTO.getDatasourceCode())
                            .schemaName(batchPlanBaseDTO.getDatasourceSchema())
                            .tableName(batchPlanBaseDTO.getObjectName())
                            .planId(batchPlanBaseDTO.getPlanId())
                            .tenantId(batchPlanFieldDTO.getTenantId())
                            .build();
                    //查询表注释，字段注释
                    DriverSession driverSession = driverSessionService.getDriverSession(batchPlanBaseDTO.getTenantId(), batchPlanBaseDTO.getDatasourceCode());
                    List<Table> tables = driverSession.tablesNameAndDesc(batchPlanBaseDTO.getDatasourceSchema());
                    tables.forEach(table -> {
                        if (batchPlanBaseDTO.getObjectName().equals(table.getTableName())) {
                            standardAimDTO.setTableDesc(table.getRemarks());
                        }
                    });
                    List<Column> columns = driverSession.columnMetaData(batchPlanBaseDTO.getDatasourceSchema(), batchPlanBaseDTO.getObjectName());
                    columns.forEach(column -> {
                        if (standardAimDTO.getFieldName().equals(column.getColumnName())) {
                            standardAimDTO.setFieldDesc(column.getRemarks());
                        }
                    });
                    standardAimRepository.insertDTOSelective(standardAimDTO);
                    //存落标关系表
                    StandardAimRelationDTO standardAimRelationDTO = StandardAimRelationDTO.builder()
                            .aimId(standardAimDTO.getAimId())
                            .aimType(REFERENCE)
                            .planId(batchPlanBaseDTO.getPlanId())
                            .planBaseId(batchPlanBaseDTO.getPlanBaseId())
                            .planRuleId(batchPlanFieldDTO.getPlanRuleId())
                            .tenantId(batchPlanFieldDTO.getTenantId())
                            .build();
                    standardAimRelationRepository.insertDTOSelective(standardAimRelationDTO);
                });
            }
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(BatchPlanFieldDTO batchPlanFieldDTO) {
        Long tenantId = batchPlanFieldDTO.getTenantId();
        batchPlanFieldRepository.updateDTOAllColumnWhereTenant(batchPlanFieldDTO, tenantId);
        if (batchPlanFieldDTO.getBatchPlanFieldLineDTOList() != null) {
            for (BatchPlanFieldLineDTO batchPlanFieldLineDTO : batchPlanFieldDTO.getBatchPlanFieldLineDTOList()) {
                if (AuditDomain.RecordStatus.update.equals(batchPlanFieldLineDTO.get_status())) {
                    batchPlanFieldLineRepository.updateDTOAllColumnWhereTenant(batchPlanFieldLineDTO, tenantId);
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
                            con.setWarningLevel(JsonUtils.object2Json(con.getWarningLevelList()));
                            batchPlanFieldConRepository.updateDTOAllColumnWhereTenant(con, tenantId);
                        } else if (AuditDomain.RecordStatus.create.equals(con.get_status())) {
                            con.setWarningLevel(JsonUtils.object2Json(con.getWarningLevelList()));
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
            conDTOList.forEach(dto -> dto.setWarningLevelList(JsonUtils.json2WarningLevel(dto.getWarningLevel())));
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
        // 查询标准规则
        Rule rule = ruleRepository.selectByPrimaryKey(ruleId);
        // 查询规则校验项
        List<RuleLine> ruleLineList = ruleLineRepository.select(RuleLine.FIELD_RULE_ID, ruleId);

        //转换
        BatchPlanFieldDTO batchPlanFieldDTO = BatchPlanFieldDTO.builder()
                .ruleCode(rule.getRuleCode())
                .ruleName(rule.getRuleName())
                .ruleDesc(rule.getRuleDesc())
                .checkType(rule.getCheckType())
                .exceptionBlock(rule.getExceptionBlock())
                .weight(rule.getWeight())
                .build();


        List<BatchPlanFieldLineDTO> batchPlanFieldLineDTOList = ruleLineList.stream().map(ruleLine -> BatchPlanFieldLineDTO.builder()
                .checkWay(ruleLine.getCheckWay())
                .checkItem(ruleLine.getCheckItem())
                .countType(ruleLine.getCountType())
                .regularExpression(ruleLine.getRegularExpression())
                .batchPlanFieldConDTOList(
                        Collections.singletonList(
                                BatchPlanFieldConDTO.builder()
                                        .compareWay(ruleLine.getCompareWay())
                                        .warningLevel(ruleLine.getWarningLevel())
                                        .warningLevelList(JsonUtils.json2WarningLevel(ruleLine.getWarningLevel()))
                                        .build()))
                .build()).collect(Collectors.toList());

        batchPlanFieldDTO.setBatchPlanFieldLineDTOList(batchPlanFieldLineDTOList);

        return batchPlanFieldDTO;
    }

    @Override
    public Page<BatchPlanFieldDTO> selectDetailList(PageRequest pageRequest, BatchPlanFieldDTO batchPlanFieldDTO) {
        Page<BatchPlanFieldDTO> pages = PageHelper.doPage(pageRequest, () -> batchPlanFieldRepository.selectDetailList(batchPlanFieldDTO));
        for (BatchPlanFieldDTO dto : pages.getContent()) {
            dto.setWarningLevelList(JsonUtils.json2WarningLevel(dto.getWarningLevel()));
        }
        return pages;
    }
}
