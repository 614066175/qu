package com.hand.hdsp.quality.infra.repository.impl;

import java.util.ArrayList;
import java.util.List;

import com.hand.hdsp.core.base.repository.impl.BaseRepositoryImpl;
import com.hand.hdsp.quality.api.dto.RuleDTO;
import com.hand.hdsp.quality.api.dto.RuleGroupDTO;
import com.hand.hdsp.quality.domain.entity.Rule;
import com.hand.hdsp.quality.domain.entity.RuleGroup;
import com.hand.hdsp.quality.domain.repository.RuleGroupRepository;
import com.hand.hdsp.quality.domain.repository.RuleRepository;
import com.hand.hdsp.quality.infra.mapper.RuleMapper;
import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.PageHelper;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import org.apache.commons.collections4.CollectionUtils;
import org.hzero.mybatis.domian.Condition;
import org.hzero.mybatis.util.Sqls;
import org.springframework.stereotype.Component;

/**
 * <p>规则表资源库实现</p>
 *
 * @author feng.liu01@hand-china.com 2020-03-24 09:31:46
 */
@Component
public class RuleRepositoryImpl extends BaseRepositoryImpl<Rule, RuleDTO> implements RuleRepository {

    private final RuleMapper ruleMapper;
    private final RuleGroupRepository ruleGroupRepository;

    public RuleRepositoryImpl(RuleMapper ruleMapper, RuleGroupRepository ruleGroupRepository) {
        this.ruleMapper = ruleMapper;
        this.ruleGroupRepository = ruleGroupRepository;
    }

    @Override
    public List<RuleDTO> list(List<String> ruleCodeList, String ruleModel, Long tenantId) {
        return ruleMapper.list(ruleCodeList, ruleModel, tenantId);
    }

    @Override
    public List<RuleDTO> listAll(RuleDTO ruleDTO) {
        return ruleMapper.listAll(ruleDTO);
    }

    @Override
    public Page<RuleDTO> listTenant(PageRequest pageRequest, RuleDTO ruleDTO) {
        return PageHelper.doPage(pageRequest, () -> ruleMapper.listTenant(ruleDTO));
    }

    @Override
    public void batchImport(List<RuleDTO> ruleDTOList) {
        List<RuleDTO> batchRuleDTOList=new ArrayList<>();
        if(CollectionUtils.isNotEmpty(ruleDTOList)){
            ruleDTOList.forEach(ruleDTO -> {
                List<RuleDTO> list = selectDTOByCondition(Condition.builder(Rule.class)
                        .andWhere(Sqls.custom()
                                .andEqualTo(Rule.FIELD_RULE_CODE, ruleDTO.getRuleCode())
                                .andEqualTo(Rule.FIELD_TENANT_ID, ruleDTO.getTenantId()))
                        .build());
                if(CollectionUtils.isNotEmpty(list)){
                    return;
                }
                List<RuleGroupDTO> ruleGroupDTOS = ruleGroupRepository.selectDTOByCondition(Condition.builder(RuleGroup.class)
                        .andWhere(Sqls.custom()
                                .andEqualTo(RuleGroup.FIELD_GROUP_CODE, ruleDTO.getGroupCode())
                                .andEqualTo(RuleGroup.FIELD_TENANT_ID, ruleDTO.getTenantId()))
                        .build());
                if (CollectionUtils.isNotEmpty(ruleGroupDTOS)) {
                    ruleDTO.setGroupId(ruleGroupDTOS.get(0).getGroupId());
                } else {
                    //创建分组
                    RuleGroupDTO ruleGroupDTO = RuleGroupDTO.builder()
                            .groupCode(ruleDTO.getGroupCode())
                            .groupDesc(ruleDTO.getGroupDesc())
                            .groupName(ruleDTO.getGroupName())
                            .parentGroupId(0L)
                            .tenantId(ruleDTO.getTenantId())
                            .build();
                    ruleGroupRepository.insertDTOSelective(ruleGroupDTO);
                    ruleDTO.setGroupId(ruleGroupDTO.getGroupId());
                }
                batchRuleDTOList.add(ruleDTO);
            });
        }
        batchInsertDTOSelective(batchRuleDTOList);
    }

}
