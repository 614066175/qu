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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

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
                            .andEqualTo(Rule.FIELD_TENANT_ID, Arrays.asList(commonGroup.getTenantId())))
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
                    List<RuleLineDTO> ruleLineDTOList = ruleLineRepository.selectDTOByCondition(Condition.builder(RuleLine.class)
                            .andWhere(Sqls.custom()
                                    .andEqualTo(RuleLine.FIELD_RULE_ID, ruleDTO.getRuleId()))
                            .build());
                    if (CollectionUtils.isNotEmpty(ruleDTOList)) {
                        BeanUtils.copyProperties(ruleDTOList.get(0), ruleDTO);
                    }
                });
            }
            return exportStandardRule(Collections.singletonList(commonGroup), ruleDTOList);
        } else {
            //平台级数据无需导入导出
//            CommonGroup commonGroup = commonGroupRepository.selectByCondition(Condition.builder().build());
        }


//        //此处为hzero导出功能，从上下文中获取projectId
//        Long projectId = ProjectHelper.getProjectId();
//        dto.setProjectId(projectId);
//        Long groupId = dto.getGroupId();
//        //0分组是所有分组
//        if (ObjectUtils.isNotEmpty(groupId) && groupId.equals(0L)) {
//            groupId = null;
//        }
//        List<RuleGroupDTO> ruleGroupDTOList = ruleGroupRepository.selectDTOByCondition(Condition.builder(RuleGroup.class)
//                .andWhere(Sqls.custom()
//                        .andEqualTo(RuleGroup.FIELD_GROUP_ID, groupId, true)
//                        .andIn(RuleGroup.FIELD_TENANT_ID, Arrays.asList(0, dto.getTenantId()))
//                        .andEqualTo(RuleGroup.FIELD_PROJECT_ID, dto.getProjectId()))
//                .build());
//        if (ObjectUtils.isNotEmpty(groupId)) {
//            int level = 1;
//            ruleGroupDTOList.forEach(ruleGroupDTO -> {
//                ruleGroupDTO.setGroupLevel(level);
//            });
//            Long parentGroupId = ruleGroupDTOList.get(0).getParentGroupId();
//            if (ObjectUtils.isNotEmpty(parentGroupId)) {
//                findParentGroups(parentGroupId, ruleGroupDTOList, level);
//            }
//            ruleGroupDTOList = ruleGroupDTOList.stream().sorted(Comparator.comparing(RuleGroupDTO::getGroupLevel).reversed()).collect(Collectors.toList());
//        } else {
//            //全量导出
//            List<RuleGroupDTO> parentRuleGroupDTOS = ruleGroupDTOList.stream()
//                    .filter(ruleGroupDTO -> (ruleGroupDTO.getParentGroupId().equals(0L)))
//                    .peek(ruleGroupDTO -> ruleGroupDTO.setParentGroupCode(null))
//                    .collect(Collectors.toList());
//            Iterator<RuleGroupDTO> iterator = parentRuleGroupDTOS.iterator();
//            if (iterator.hasNext()) {
//                findChildGroups(iterator.next(), parentRuleGroupDTOS);
//            }
//        }
//        ruleGroupDTOList.forEach(ruleGroupDTO -> {
//            //查询某一分组下的，筛选后的标准规则
//            List<RuleDTO> ruleDTOList = ruleRepository.selectDTOByCondition(Condition.builder(Rule.class)
//                    .andWhere(Sqls.custom()
//                            .andEqualTo(Rule.FIELD_GROUP_ID, ruleGroupDTO.getGroupId())
//                            .andEqualTo(Rule.FIELD_PROJECT_ID, ruleGroupDTO.getProjectId())
//                            .andIn(Rule.FIELD_TENANT_ID, Arrays.asList(0, dto.getTenantId())))
//                    .andWhere(Sqls.custom()
//                            .andLike(Rule.FIELD_RULE_CODE, dto.getRuleCode(), true)
//                            .andLike(Rule.FIELD_RULE_NAME, dto.getRuleName(), true)
//                            .andLike(Rule.FIELD_RULE_DESC, dto.getRuleDesc(), true)
//                            .andEqualTo(Rule.FIELD_CHECK_TYPE, dto.getCheckType(), true)
//                            .andEqualTo(Rule.FIELD_EXCEPTION_BLOCK, dto.getExceptionBlock(), true)
//                            .andEqualTo(Rule.FIELD_WEIGHT, dto.getWeight(), true))
//                    .build());
//            //设置分组的父分组编码
//            if (ObjectUtils.isNotEmpty(ruleGroupDTO.getParentGroupId())) {
//                if (ruleGroupDTO.getParentGroupId() != 0) {
//                    RuleGroupDTO parentRuleGroupDTO = ruleGroupRepository.selectDTOByPrimaryKey(ruleGroupDTO.getParentGroupId());
//                    ruleGroupDTO.setParentGroupCode(parentRuleGroupDTO.getGroupCode());
//                } else {
//                    //0目录：所有分组
//                    ruleGroupDTO.setParentGroupCode(RuleGroup.ROOT_RULE_GROUP.getGroupCode());
//                }
//            }
//            //查询标准规则的告警配置
//            ruleDTOList.forEach(ruleDTO -> {
//                List<RuleLineDTO> ruleLineDTOList = ruleLineRepository.selectDTOByCondition(Condition.builder(RuleLine.class)
//                        .andWhere(Sqls.custom()
//                                .andEqualTo(RuleLine.FIELD_RULE_ID, ruleDTO.getRuleId()))
//                        .build());
//                ruleLineDTOList.forEach(ruleLineDTO -> BeanUtils.copyProperties(ruleLineDTO, ruleDTO));
//            });
//            ruleGroupDTO.setRuleDTOList(ruleDTOList);
//        });
//        return ruleGroupDTOList;
        return null;
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
