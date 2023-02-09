package com.hand.hdsp.quality.infra.datafix;

import com.hand.hdsp.core.domain.entity.CommonGroup;
import com.hand.hdsp.core.domain.repository.CommonGroupRepository;
import com.hand.hdsp.core.infra.datafix.GroupDataFixer;
import com.hand.hdsp.quality.domain.entity.Rule;
import com.hand.hdsp.quality.domain.entity.RuleGroup;
import com.hand.hdsp.quality.domain.repository.RuleGroupRepository;
import com.hand.hdsp.quality.domain.repository.RuleRepository;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.hand.hdsp.core.infra.constant.CommonGroupConstants.GroupType.STANDARD_RULE;

/**
 * @Description: 词根分组修复接口
 * @author XIN.SHENG01@HAND-CHINA.COM 2023/02/07 11:24
 */
@Component(value = "STANDARD_RULE_FIXER")
public class RuleGroupFixer implements GroupDataFixer {
    private final RuleGroupRepository ruleGroupRepository;
    private final CommonGroupRepository commonGroupRepository;

    @Autowired
    private RuleRepository ruleRepository;

    public RuleGroupFixer(RuleGroupRepository ruleGroupRepository, CommonGroupRepository commonGroupRepository) {
        this.ruleGroupRepository = ruleGroupRepository;
        this.commonGroupRepository = commonGroupRepository;
    }


    @Override
    public void groupDataFix() {
        // 1.获取原始分组数据
        // 2.将原始数据按照分组层级插入新表，获取id映射关系
        // 3.根据映射关系去修改内容的分组id

        //查询所有标准规则分组
        List<RuleGroup> groups = ruleGroupRepository.select(RuleGroup.builder().build());
        if (CollectionUtils.isEmpty(groups)) {
            return;
        }
        //定义映射关系
        Map<Long, Long> groupMap = new HashMap<>(groups.size());
        Map<Long, RuleGroup> standardGroupMap = groups.stream().collect(Collectors.toMap(RuleGroup::getGroupId, Function.identity(), (key1, key2) -> key2));
        Set<Long> fixedGroup = new HashSet<>();
        for (RuleGroup group : groups) {
            //判断是不是处理过，处理了直接跳过
            if (fixedGroup.contains(group.getGroupId())) {
                continue;
            }
            //递归修复分组数据
            doFix(group, standardGroupMap, groupMap, fixedGroup);
        }
        //修复分组数据
        List<Rule> ruleList = ruleRepository.selectAll();
        if (CollectionUtils.isNotEmpty(ruleList)) {
            ruleList.forEach(rule -> {
                rule.setGroupId(groupMap.getOrDefault(rule.getGroupId(), 0L));
            });
            ruleRepository.batchUpdateOptional(ruleList, Rule.FIELD_GROUP_ID);
        }
    }

    private void doFix(RuleGroup group, Map<Long, RuleGroup> standardGroupMap, Map<Long, Long> groupMap, Set<Long> fixedGroup) {
        if (group.getParentGroupId() != null && standardGroupMap.get(group.getParentGroupId()) != null) {
            //存在父分组，先处理父分组
            doFix(standardGroupMap.get(group.getParentGroupId()), standardGroupMap, groupMap, fixedGroup);
        }
        if (fixedGroup.contains(group.getGroupId())) {
            return;
        }
        //构建新分组数据

        CommonGroup commonGroup = CommonGroup.builder().groupType(STANDARD_RULE)
                .groupName(group.getGroupName())
                .tenantId(group.getTenantId())
                .projectId(group.getProjectId())
                .build();
        //从映射关系中获取旧父分组对应的新id
        if (group.getParentGroupId() == null || group.getParentGroupId() <= 0) {
            commonGroup.setParentGroupId(0L);
        } else {
            Long newParentGroupId = groupMap.get(group.getParentGroupId());
            commonGroup.setParentGroupId(Optional.ofNullable(newParentGroupId).orElse(0L));
        }
        //构建全路径
        CommonGroup parentGroup = commonGroupRepository.selectByPrimaryKey(commonGroup.getParentGroupId());
        if (parentGroup == null) {
            commonGroup.setGroupPath(commonGroup.getGroupName());
        } else {
            commonGroup.setGroupPath(String.format("%s/%s", parentGroup.getGroupPath(), commonGroup.getGroupName()));
        }
        //判断分组存不存在
        CommonGroup exist = commonGroupRepository.selectOne(commonGroup);
        if (exist == null) {
            commonGroupRepository.insertSelective(commonGroup);
        } else {
            commonGroup.setGroupId(exist.getGroupId());
        }
        fixedGroup.add(group.getGroupId());
        groupMap.put(group.getGroupId(), commonGroup.getGroupId());
    }
}
