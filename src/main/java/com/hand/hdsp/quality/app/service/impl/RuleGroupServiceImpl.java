package com.hand.hdsp.quality.app.service.impl;

import com.hand.hdsp.core.util.ProjectHelper;
import com.hand.hdsp.quality.api.dto.DataFieldGroupDTO;
import com.hand.hdsp.quality.api.dto.RuleDTO;
import com.hand.hdsp.quality.api.dto.RuleGroupDTO;
import com.hand.hdsp.quality.api.dto.RuleLineDTO;
import com.hand.hdsp.quality.app.service.RuleGroupService;
import com.hand.hdsp.quality.domain.entity.Rule;
import com.hand.hdsp.quality.domain.entity.RuleGroup;
import com.hand.hdsp.quality.domain.entity.RuleLine;
import com.hand.hdsp.quality.domain.repository.RuleGroupRepository;
import com.hand.hdsp.quality.domain.repository.RuleLineRepository;
import com.hand.hdsp.quality.domain.repository.RuleRepository;
import com.hand.hdsp.quality.infra.constant.ErrorCode;
import io.choerodon.core.exception.CommonException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.hzero.boot.platform.lov.annotation.ProcessLovValue;
import org.hzero.export.vo.ExportParam;
import org.hzero.mybatis.domian.Condition;
import org.hzero.mybatis.util.Sqls;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 规则分组表应用服务默认实现
 *
 * @author wuzhong26857
 * @version 1.0
 * @date 2020/3/23 13:36
 */
@Service
@Slf4j
public class RuleGroupServiceImpl implements RuleGroupService {

    private final RuleGroupRepository ruleGroupRepository;
    private final RuleRepository ruleRepository;
    private final RuleLineRepository ruleLineRepository;

    public RuleGroupServiceImpl(RuleGroupRepository ruleGroupRepository,
                                RuleRepository ruleRepository,
                                RuleLineRepository ruleLineRepository) {
        this.ruleGroupRepository = ruleGroupRepository;
        this.ruleRepository = ruleRepository;
        this.ruleLineRepository = ruleLineRepository;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int delete(RuleGroupDTO ruleGroupDTO) {
        //分组或子分组存在标准规则不可删除;不存在或存在空的分组则删除，并同时删除空的分组
        //遍历获取子目录集合
        Long projectId = ProjectHelper.getProjectId();
        List<RuleGroupDTO> ruleGroups = new ArrayList<>();
        findChildGroups(ruleGroupDTO, ruleGroups);
        ruleGroups.add(ruleGroupDTO);
        //校验分组和子分组下是否有标准规则
        ruleGroups.forEach(ruleGroupDto -> {
            List<RuleDTO> ruleDTOList = ruleRepository.selectDTOByCondition(Condition.builder(Rule.class).andWhere(Sqls.custom()).andWhere(Sqls.custom()
                    .andEqualTo(Rule.FIELD_TENANT_ID, ruleGroupDTO.getTenantId())
                    .andEqualTo(Rule.FIELD_PROJECT_ID, projectId)
                    .andEqualTo(Rule.FIELD_GROUP_ID, ruleGroupDto.getGroupId())
            ).build());
            if (CollectionUtils.isNotEmpty(ruleDTOList)) {
                throw new CommonException(ErrorCode.EXISTS_OTHER_GROUP_OR_RULE);
            }
        });
        return ruleGroupRepository.batchDTODeleteByPrimaryKey(ruleGroups);
    }

    private void findChildGroups(RuleGroupDTO ruleGroupDTO, List<RuleGroupDTO> ruleGroups) {
        List<RuleGroupDTO> ruleGroupDTOList = ruleGroupRepository.selectDTOByCondition(Condition.builder(RuleGroup.class).andWhere(Sqls.custom()
                        .andEqualTo(RuleGroup.FIELD_PROJECT_ID, ruleGroupDTO.getProjectId())
                        .andEqualTo(RuleGroup.FIELD_TENANT_ID, ruleGroupDTO.getTenantId())
                        .andEqualTo(RuleGroup.FIELD_PARENT_GROUP_ID, ruleGroupDTO.getGroupId()))
                .build());
        if (CollectionUtils.isNotEmpty(ruleGroupDTOList)) {
            ruleGroups.addAll(ruleGroupDTOList);
            for (RuleGroupDTO ruleGroupDto : ruleGroupDTOList) {
                findChildGroups(ruleGroupDto, ruleGroups);
            }
        }
    }

    @Override
    public List<RuleGroup> selectList(RuleGroup ruleGroup) {
        List<RuleGroup> list = ruleGroupRepository.select(ruleGroup);
        list.add(RuleGroup.ROOT_RULE_GROUP);
        return list;
    }

    @Override
    public List<RuleGroup> listNoPage(RuleGroup ruleGroup) {
        List<RuleGroup> list = ruleGroupRepository.list(ruleGroup);
        list.add(RuleGroup.ROOT_RULE_GROUP);
        return list;
    }

    @Override
    @ProcessLovValue(targetField = {"ruleDTOList"})
    public List<RuleGroupDTO> export(RuleDTO dto, ExportParam exportParam) {
        //此处为hzero导出功能，从上下文中获取projectId
        Long projectId = ProjectHelper.getProjectId();
        dto.setProjectId(projectId);
        Long groupId = dto.getGroupId();
        //0分组是所有分组
        if(ObjectUtils.isNotEmpty(groupId) && groupId.equals(0L)){
            groupId = null;
        }
        List<RuleGroupDTO> ruleGroupDTOList = ruleGroupRepository.selectDTOByCondition(Condition.builder(RuleGroup.class)
                .andWhere(Sqls.custom()
                        .andEqualTo(RuleGroup.FIELD_GROUP_ID, groupId, true)
                        .andIn(RuleGroup.FIELD_TENANT_ID, Arrays.asList(0, dto.getTenantId()))
                        .andEqualTo(RuleGroup.FIELD_PROJECT_ID, dto.getProjectId()))
                .build());
        if(ObjectUtils.isNotEmpty(groupId)){
            int level = 1;
            ruleGroupDTOList.forEach(ruleGroupDTO -> {
                ruleGroupDTO.setGroupLevel(level);
            });
            Long parentGroupId = ruleGroupDTOList.get(0).getParentGroupId();
            if(ObjectUtils.isNotEmpty(parentGroupId)){
                findParentGroups(parentGroupId,ruleGroupDTOList,level);
            }
            ruleGroupDTOList = ruleGroupDTOList.stream().sorted(Comparator.comparing(RuleGroupDTO::getGroupLevel).reversed()).collect(Collectors.toList());
        }else {
            //全量导出
            List<RuleGroupDTO> parentRuleGroupDTOS = ruleGroupDTOList.stream()
                    .filter(ruleGroupDTO -> (ruleGroupDTO.getParentGroupId().equals(0L)))
                    .peek(ruleGroupDTO -> ruleGroupDTO.setParentGroupCode(null))
                    .collect(Collectors.toList());
            Iterator<RuleGroupDTO> iterator = parentRuleGroupDTOS.iterator();
            if(iterator.hasNext()){
                findChildGroups(iterator.next(),parentRuleGroupDTOS);
            }
        }
        ruleGroupDTOList.forEach(ruleGroupDTO -> {
            //查询某一分组下的，筛选后的标准规则
            List<RuleDTO> ruleDTOList = ruleRepository.selectDTOByCondition(Condition.builder(Rule.class)
                    .andWhere(Sqls.custom()
                            .andEqualTo(Rule.FIELD_GROUP_ID, ruleGroupDTO.getGroupId())
                            .andEqualTo(Rule.FIELD_PROJECT_ID, ruleGroupDTO.getProjectId())
                            .andIn(Rule.FIELD_TENANT_ID, Arrays.asList(0, dto.getTenantId())))
                    .andWhere(Sqls.custom()
                            .andLike(Rule.FIELD_RULE_CODE, dto.getRuleCode(), true)
                            .andLike(Rule.FIELD_RULE_NAME, dto.getRuleName(), true)
                            .andLike(Rule.FIELD_RULE_DESC, dto.getRuleDesc(), true)
                            .andEqualTo(Rule.FIELD_CHECK_TYPE, dto.getCheckType(), true)
                            .andEqualTo(Rule.FIELD_EXCEPTION_BLOCK, dto.getExceptionBlock(), true)
                            .andEqualTo(Rule.FIELD_WEIGHT, dto.getWeight(), true))
                    .build());
            //设置分组的父分组编码
            if (ObjectUtils.isNotEmpty(ruleGroupDTO.getParentGroupId())) {
                if (ruleGroupDTO.getParentGroupId() != 0) {
                    RuleGroupDTO parentRuleGroupDTO = ruleGroupRepository.selectDTOByPrimaryKey(ruleGroupDTO.getParentGroupId());
                    ruleGroupDTO.setParentGroupCode(parentRuleGroupDTO.getGroupCode());
                } else {
                    //0目录：所有分组
                    ruleGroupDTO.setParentGroupCode(RuleGroup.ROOT_RULE_GROUP.getGroupCode());
                }
            }
            //查询标准规则的告警配置
            ruleDTOList.forEach(ruleDTO -> {
                List<RuleLineDTO> ruleLineDTOList = ruleLineRepository.selectDTOByCondition(Condition.builder(RuleLine.class)
                        .andWhere(Sqls.custom()
                                .andEqualTo(RuleLine.FIELD_RULE_ID, ruleDTO.getRuleId()))
                        .build());
                ruleLineDTOList.forEach(ruleLineDTO -> BeanUtils.copyProperties(ruleLineDTO, ruleDTO));
            });
            ruleGroupDTO.setRuleDTOList(ruleDTOList);
        });
        return ruleGroupDTOList;
    }

    @Override
    public int create(RuleGroupDTO dto) {

        if (dto.getParentGroupId() == null) {
            dto.setParentGroupId(0L);
        }
         // 校验父目录下是否有标准
//        if (ruleGroupDTO.getParentGroupId() != null) {
//            RuleDTO dto = ruleGroupRepository.selectDTOByPrimaryKey(standardGroupDTO.getParentGroupId());
//            existStandard(dto);
//        }
        // 校验编码存在
        List<RuleGroupDTO> dtoList = ruleGroupRepository.selectDTOByCondition(Condition.builder(RuleGroup.class)
                .andWhere(Sqls.custom()
                        .andEqualTo(RuleGroup.FIELD_TENANT_ID, dto.getTenantId())
                        .andEqualTo(RuleGroup.FIELD_GROUP_CODE, dto.getGroupCode())
                        .andEqualTo(RuleGroup.FIELD_PROJECT_ID, dto.getProjectId()))
                .build());
        if (CollectionUtils.isNotEmpty(dtoList)) {
            throw new CommonException(ErrorCode.CODE_ALREADY_EXISTS);
        }
        // 校验名称存在
        dtoList = ruleGroupRepository.selectDTOByCondition(Condition.builder(RuleGroup.class)
                .andWhere(Sqls.custom()
                        .andEqualTo(RuleGroup.FIELD_TENANT_ID, dto.getTenantId())
                        .andEqualTo(RuleGroup.FIELD_GROUP_NAME, dto.getGroupName())
                        .andEqualTo(RuleGroup.FIELD_PROJECT_ID, dto.getProjectId())
                        .andEqualTo(RuleGroup.FIELD_PARENT_GROUP_ID, dto.getParentGroupId()))
                .build());
        if (CollectionUtils.isNotEmpty(dtoList)) {
            throw new CommonException(ErrorCode.GROUP_NAME_ALREADY_EXIST);
        }
        return ruleGroupRepository.insertDTOSelective(dto);
    }

    private void findParentGroups(Long parentGroupId, List<RuleGroupDTO> ruleGroupDTOList, int level) {
        List<RuleGroupDTO> ruleGroupDTOS = ruleGroupRepository.selectDTOByCondition(Condition.builder(RuleGroup.class).andWhere(Sqls.custom()
                        .andEqualTo(RuleGroup.FIELD_GROUP_ID, parentGroupId))
                .build());
        level++;
        if(CollectionUtils.isNotEmpty(ruleGroupDTOS)){
            //存在父分组
            int finalLevel = level;
            ruleGroupDTOS.forEach(ruleGroupDTO -> {
                if(ObjectUtils.isNotEmpty(ruleGroupDTO.getParentGroupId())){
                    ruleGroupDTO.setGroupLevel(finalLevel);
                    if(0 == ruleGroupDTO.getParentGroupId()){
                        //父分组为所有分组
                        ruleGroupDTO.setParentGroupCode("root");
                        ruleGroupDTOList.add(ruleGroupDTO);
                        return;
                    }
                    ruleGroupDTOList.add(ruleGroupDTO);
                    findParentGroups(ruleGroupDTO.getParentGroupId(),ruleGroupDTOList, finalLevel);
                }
            });
        }
    }
}
