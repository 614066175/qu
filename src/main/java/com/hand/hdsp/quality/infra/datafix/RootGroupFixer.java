package com.hand.hdsp.quality.infra.datafix;

import com.hand.hdsp.core.domain.entity.CommonGroup;
import com.hand.hdsp.core.domain.repository.CommonGroupRepository;
import com.hand.hdsp.core.infra.datafix.GroupDataFixer;
import com.hand.hdsp.quality.domain.entity.StandardGroup;
import com.hand.hdsp.quality.domain.repository.RootRepository;
import com.hand.hdsp.quality.domain.repository.StandardGroupRepository;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.hand.hdsp.core.infra.constant.CommonGroupConstants.GroupType.ROOT_STANDARD;
import static com.hand.hdsp.quality.infra.constant.StandardConstant.StandardType.ROOT;

/**
 * @Description: 词根分组修复接口
 * @author XIN.SHENG01@HAND-CHINA.COM 2023/02/07 11:24
 */
@Component(value = "ROOT_FIXER")
public class RootGroupFixer implements GroupDataFixer {
    private final StandardGroupRepository standardGroupRepository;
    private final RootRepository rootRepository;
    private final CommonGroupRepository commonGroupRepository;

    public RootGroupFixer(StandardGroupRepository standardGroupRepository, RootRepository rootRepository, CommonGroupRepository commonGroupRepository) {
        this.standardGroupRepository = standardGroupRepository;
        this.rootRepository = rootRepository;
        this.commonGroupRepository = commonGroupRepository;
    }

    @Override
    public void groupDataFix() {
        // 1.获取原始分组数据
        // 2.将原始数据按照分组层级插入新表，获取id映射关系
        // 3.根据映射关系去修改内容的分组id

        //查询所有数据标准分组
        List<StandardGroup> groups = standardGroupRepository.select(StandardGroup.builder().standardType(ROOT).build());
        if (CollectionUtils.isEmpty(groups)) {
            return;
        }
        //定义映射关系
        Map<Long, Long> groupMap = new HashMap<>(groups.size());
        Map<Long, StandardGroup> standardGroupMap = groups.stream().collect(Collectors.toMap(StandardGroup::getGroupId, Function.identity(), (key1, key2) -> key2));
        Set<Long> fixedGroup = new HashSet<>();
        for (StandardGroup group : groups) {
            //判断是不是处理过，处理了直接跳过
            if (fixedGroup.contains(group.getGroupId())) {
                continue;
            }
            //递归修复分组数据
            doFix(group, standardGroupMap, groupMap, fixedGroup);
        }
        //修复分组数据
//        List<Root> rootList = rootRepository.selectAll();
//        if (CollectionUtils.isNotEmpty(rootList)) {
//            rootList.forEach(root -> {
//                root.setGroupId(groupMap.getOrDefault(root.getGroupId(), 0L));
//            });
//            rootRepository.batchUpdateOptional(rootList, Root.FIELD_GROUP_ID);
//        }
    }

    private void doFix(StandardGroup group, Map<Long, StandardGroup> standardGroupMap, Map<Long, Long> groupMap, Set<Long> fixedGroup) {
        if (group.getParentGroupId() != null && standardGroupMap.get(group.getParentGroupId()) != null) {
            //存在父分组，先处理父分组
            doFix(standardGroupMap.get(group.getParentGroupId()), standardGroupMap, groupMap, fixedGroup);
        }
        if (fixedGroup.contains(group.getGroupId())) {
            return;
        }
        //构建新分组数据

        CommonGroup commonGroup = CommonGroup.builder().groupType(ROOT_STANDARD)
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
        commonGroupRepository.insertSelective(commonGroup);
        fixedGroup.add(group.getGroupId());
        groupMap.put(group.getGroupId(), commonGroup.getGroupId());
    }
}
