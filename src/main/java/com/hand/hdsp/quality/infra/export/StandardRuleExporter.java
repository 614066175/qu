package com.hand.hdsp.quality.infra.export;

import com.hand.hdsp.core.domain.entity.CommonGroup;
import com.hand.hdsp.core.domain.repository.CommonGroupRepository;
import com.hand.hdsp.quality.api.dto.RuleDTO;
import com.hand.hdsp.quality.api.dto.RuleLineDTO;
import com.hand.hdsp.quality.domain.entity.Rule;
import com.hand.hdsp.quality.domain.entity.RuleLine;
import com.hand.hdsp.quality.domain.repository.RuleLineRepository;
import com.hand.hdsp.quality.domain.repository.RuleRepository;
import com.hand.hdsp.quality.infra.export.dto.StandardRuleExportDTO;
import org.apache.commons.collections4.CollectionUtils;
import org.hzero.mybatis.domian.Condition;
import org.hzero.mybatis.util.Sqls;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

import static com.hand.hdsp.core.infra.constant.CommonGroupConstants.GroupType.STANDARD_RULE;

/**
 * @Title: StandardRuleExporter
 * @Description:
 * @author: lgl
 * @date: 2023/2/8 15:26
 */
@Component
public class StandardRuleExporter implements Exporter<RuleDTO, List<StandardRuleExportDTO>> {
    private final CommonGroupRepository commonGroupRepository;
    private final RuleRepository ruleRepository;
    private final RuleLineRepository ruleLineRepository;

    public StandardRuleExporter(CommonGroupRepository commonGroupRepository, RuleRepository ruleRepository, RuleLineRepository ruleLineRepository) {
        this.commonGroupRepository = commonGroupRepository;
        this.ruleRepository = ruleRepository;
        this.ruleLineRepository = ruleLineRepository;
    }

    @Override
    public List<StandardRuleExportDTO> export(RuleDTO dto) {
        //有分组id按分组导出
        //没有导出全量 (标准数据查询)
        if (dto.getGroupId() != null && dto.getGroupId() != 0) {
            //获取这个分组
            CommonGroup commonGroup = commonGroupRepository.selectByPrimaryKey(dto.getGroupId());
            //条件查询获取这个分组下的标准数据
            List<RuleDTO> ruleDTOList = ruleRepository.selectDTOByCondition(Condition.builder(Rule.class)
                    .andWhere(Sqls.custom()
                            .andEqualTo(Rule.FIELD_GROUP_ID, commonGroup.getGroupId())
                            .andEqualTo(Rule.FIELD_PROJECT_ID, commonGroup.getProjectId())
                            .andIn(Rule.FIELD_TENANT_ID, Arrays.asList(0L, commonGroup.getTenantId())))
                    .andWhere(Sqls.custom()
                            .andLike(Rule.FIELD_RULE_CODE, dto.getRuleCode(), true)
                            .andLike(Rule.FIELD_RULE_NAME, dto.getRuleName(), true)
                            .andLike(Rule.FIELD_RULE_DESC, dto.getRuleDesc(), true)
                            .andEqualTo(Rule.FIELD_CHECK_TYPE, dto.getCheckType(), true)
                            .andEqualTo(Rule.FIELD_EXCEPTION_BLOCK, dto.getExceptionBlock(), true)
                            .andEqualTo(Rule.FIELD_WEIGHT, dto.getWeight(), true))
                    .build());
            if (CollectionUtils.isNotEmpty(ruleDTOList)) {
                //查询标准规则的告警配置默认取一个，拉平头行
                ruleDTOList.forEach(ruleDTO -> {
                    ruleDTO.setGroupPath(commonGroup.getGroupPath());
                    if (ruleDTO.getTenantId().equals(dto.getTenantId())) {
                        //如果导出数据是当前租户的数据
                        ruleDTO.setIsPlatformFlag("N");
                    } else {
                        ruleDTO.setIsPlatformFlag("Y");
                    }
                    List<RuleLineDTO> ruleLineDTOList = ruleLineRepository.selectDTOByCondition(Condition.builder(RuleLine.class)
                            .andWhere(Sqls.custom()
                                    .andEqualTo(RuleLine.FIELD_RULE_ID, ruleDTO.getRuleId()))
                            .build());
                    if (CollectionUtils.isNotEmpty(ruleLineDTOList)) {
                        BeanUtils.copyProperties(ruleLineDTOList.get(0), ruleDTO);
                    }
                });
            }
            return exportStandardRule(Collections.singletonList(commonGroup), ruleDTOList);
        } else {
            //导出所有分组数据
            List<CommonGroup> commonGroups = commonGroupRepository.selectByCondition(Condition.builder(CommonGroup.class)
                    .andWhere(Sqls.custom()
                            .andIn(Rule.FIELD_TENANT_ID, Arrays.asList(0L, dto.getTenantId()))
                            .andEqualTo(CommonGroup.FIELD_PROJECT_ID, dto.getProjectId())
                            .andEqualTo(CommonGroup.FIELD_GROUP_TYPE, STANDARD_RULE))
                    .build());
            if (CollectionUtils.isEmpty(commonGroups)) {
                return new ArrayList<>();
            }
            Map<Long, String> groupPathMap = commonGroups.stream().collect(Collectors.toMap(CommonGroup::getGroupId, CommonGroup::getGroupPath, (key1, key2) -> key2));
            //查询符合条件的标准规则数据
            //条件查询获取这个分组下的标准数据
            List<RuleDTO> ruleDTOList = ruleRepository.selectDTOByCondition(Condition.builder(Rule.class)
                    .andWhere(Sqls.custom()
                            .andEqualTo(Rule.FIELD_PROJECT_ID, dto.getProjectId())
                            .andIn(Rule.FIELD_TENANT_ID, Arrays.asList(0L, dto.getTenantId())))
                    .andWhere(Sqls.custom()
                            .andLike(Rule.FIELD_RULE_CODE, dto.getRuleCode(), true)
                            .andLike(Rule.FIELD_RULE_NAME, dto.getRuleName(), true)
                            .andLike(Rule.FIELD_RULE_DESC, dto.getRuleDesc(), true)
                            .andEqualTo(Rule.FIELD_CHECK_TYPE, dto.getCheckType(), true)
                            .andEqualTo(Rule.FIELD_EXCEPTION_BLOCK, dto.getExceptionBlock(), true)
                            .andEqualTo(Rule.FIELD_WEIGHT, dto.getWeight(), true))
                    .build());
            if (CollectionUtils.isNotEmpty(ruleDTOList)) {
                //查询标准规则的告警配置默认取一个，拉平头行
                ruleDTOList.forEach(ruleDTO -> {
                    ruleDTO.setGroupPath(groupPathMap.get(ruleDTO.getGroupId()));
                    if (ruleDTO.getTenantId().equals(dto.getTenantId())) {
                        //如果导出数据是当前租户的数据
                        ruleDTO.setIsPlatformFlag("N");
                    } else {
                        ruleDTO.setIsPlatformFlag("Y");
                    }
                    List<RuleLineDTO> ruleLineDTOList = ruleLineRepository.selectDTOByCondition(Condition.builder(RuleLine.class)
                            .andWhere(Sqls.custom()
                                    .andEqualTo(RuleLine.FIELD_RULE_ID, ruleDTO.getRuleId()))
                            .build());
                    if (CollectionUtils.isNotEmpty(ruleLineDTOList)) {
                        BeanUtils.copyProperties(ruleLineDTOList.get(0), ruleDTO);
                    }
                });
            }
            return exportStandardRule(commonGroups, ruleDTOList);
        }
    }

    public List<StandardRuleExportDTO> exportStandardRule(List<CommonGroup> commonGroups, List<RuleDTO> ruleDTOList) {
        List<StandardRuleExportDTO> standardRuleExportDTOList = new ArrayList<>();
        commonGroups.forEach(commonGroup -> {
            StandardRuleExportDTO standardRuleExportDTO = new StandardRuleExportDTO();
            BeanUtils.copyProperties(commonGroup, standardRuleExportDTO);
            int i = standardRuleExportDTO.getGroupPath().lastIndexOf("/");
            if (i > 0) {
                standardRuleExportDTO.setParentGroupPath(standardRuleExportDTO.getGroupPath().substring(0, i));
            }
            standardRuleExportDTOList.add(standardRuleExportDTO);
        });
        standardRuleExportDTOList.get(0).setRuleDTOList(ruleDTOList);
        return standardRuleExportDTOList;
    }
}
