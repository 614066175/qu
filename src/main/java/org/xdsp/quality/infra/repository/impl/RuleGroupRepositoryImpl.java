package org.xdsp.quality.infra.repository.impl;

import org.apache.commons.collections4.CollectionUtils;
import org.hzero.mybatis.domian.Condition;
import org.hzero.mybatis.util.Sqls;
import org.springframework.stereotype.Component;
import org.xdsp.core.base.repository.impl.BaseRepositoryImpl;
import org.xdsp.quality.api.dto.RuleGroupDTO;
import org.xdsp.quality.domain.entity.RuleGroup;
import org.xdsp.quality.domain.repository.RuleGroupRepository;
import org.xdsp.quality.infra.mapper.RuleGroupMapper;

import java.util.ArrayList;
import java.util.List;

/**
 * 规则分组表资源库实现
 *
 * @author wuzhong26857
 * @version 1.0
 * @date 2020/3/23 13:39
 */
@Component
public class RuleGroupRepositoryImpl extends BaseRepositoryImpl<RuleGroup, RuleGroupDTO> implements RuleGroupRepository {

    private final RuleGroupMapper ruleGroupMapper;

    public RuleGroupRepositoryImpl(RuleGroupMapper ruleGroupMapper) {
        this.ruleGroupMapper = ruleGroupMapper;
    }


    @Override
    public List<RuleGroup> list(RuleGroup ruleGroup) {
        return ruleGroupMapper.list(ruleGroup);
    }

    @Override
    public void batchImport(List<RuleGroupDTO> ruleGroupDTOList) {
        List<RuleGroupDTO> list = new ArrayList<>(ruleGroupDTOList.size());
        ruleGroupDTOList.forEach(ruleGroupDTO -> {
            List<RuleGroupDTO> ruleGroupDTOS = selectDTOByCondition(Condition.builder(RuleGroup.class)
                    .andWhere(Sqls.custom()
                            .andEqualTo(RuleGroup.FIELD_GROUP_CODE, ruleGroupDTO.getGroupCode())
                            .andEqualTo(RuleGroup.FIELD_TENANT_ID, ruleGroupDTO.getTenantId()))
                    .build());
            if (CollectionUtils.isNotEmpty(ruleGroupDTOS)) {
                //创建分组
                RuleGroupDTO dto = RuleGroupDTO.builder()
                        .groupCode(ruleGroupDTO.getGroupCode())
                        .groupDesc(ruleGroupDTO.getGroupDesc())
                        .groupName(ruleGroupDTO.getGroupName())
                        .parentGroupId(0L)
                        .tenantId(ruleGroupDTO.getTenantId())
                        .build();
                list.add(dto);
            }
        });
        batchInsertDTOSelective(list);
    }
}
