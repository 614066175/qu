package org.xdsp.quality.infra.repository.impl;

import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.PageHelper;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import org.apache.commons.collections4.CollectionUtils;
import org.hzero.mybatis.domian.Condition;
import org.hzero.mybatis.util.Sqls;
import org.springframework.stereotype.Component;
import org.xdsp.core.base.repository.impl.BaseRepositoryImpl;
import org.xdsp.quality.api.dto.RuleDTO;
import org.xdsp.quality.api.dto.RuleGroupDTO;
import org.xdsp.quality.api.dto.RuleLineDTO;
import org.xdsp.quality.domain.entity.Rule;
import org.xdsp.quality.domain.entity.RuleGroup;
import org.xdsp.quality.domain.repository.RuleGroupRepository;
import org.xdsp.quality.domain.repository.RuleLineRepository;
import org.xdsp.quality.domain.repository.RuleRepository;
import org.xdsp.quality.infra.mapper.RuleMapper;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>规则表资源库实现</p>
 *
 * @author feng.liu01@hand-china.com 2020-03-24 09:31:46
 */
@Component
public class RuleRepositoryImpl extends BaseRepositoryImpl<Rule, RuleDTO> implements RuleRepository {

    private final RuleMapper ruleMapper;
    private final RuleGroupRepository ruleGroupRepository;
    private final RuleLineRepository ruleLineRepository;

    public RuleRepositoryImpl(RuleMapper ruleMapper, RuleGroupRepository ruleGroupRepository, RuleLineRepository ruleLineRepository) {
        this.ruleMapper = ruleMapper;
        this.ruleGroupRepository = ruleGroupRepository;
        this.ruleLineRepository = ruleLineRepository;
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
        List<RuleLineDTO> ruleLineDTOList=new ArrayList<>();
        if(CollectionUtils.isNotEmpty(ruleDTOList)){
            for (RuleDTO ruleDTO : ruleDTOList) {
                List<RuleGroupDTO> ruleGroupDTOS = ruleGroupRepository.selectDTOByCondition(Condition.builder(RuleGroup.class)
                        .andWhere(Sqls.custom()
                                .andEqualTo(RuleGroup.FIELD_GROUP_CODE, ruleDTO.getGroupCode())
                                .andEqualTo(RuleGroup.FIELD_TENANT_ID, ruleDTO.getTenantId())
                                .andEqualTo(RuleGroup.FIELD_PROJECT_ID,ruleDTO.getProjectId()))
                        .build());
                if (CollectionUtils.isNotEmpty(ruleGroupDTOS)) {
                    ruleDTO.setGroupId(ruleGroupDTOS.get(0).getGroupId());
                } else {
                    //跳过没有分组的规则
                    continue;
                }
                batchRuleDTOList.add(ruleDTO);
            }
        }
        //插入序列化返回业务属性字段拿不到
        List<RuleDTO> list = batchInsertDTOSelective(batchRuleDTOList);
        for (int i=0;i<batchRuleDTOList.size();i++) {
            //获取规则行
            RuleDTO ruleDTO = batchRuleDTOList.get(i);
            RuleLineDTO ruleLineDTO = RuleLineDTO.builder()
                    .ruleId(list.get(i).getRuleId())
                    .checkWay(ruleDTO.getCheckWay())
                    .checkItem(ruleDTO.getCheckItem())
                    .compareWay(ruleDTO.getCompareWay())
                    .countType(ruleDTO.getCountType())
                    .regularExpression(ruleDTO.getRegularExpression())
                    .warningLevel(ruleDTO.getWarningLevel())
                    .tenantId(ruleDTO.getTenantId())
                    .projectId(ruleDTO.getProjectId())
                    .build();
            ruleLineDTOList.add(ruleLineDTO);
        }
        ruleLineRepository.batchInsertDTOSelective(ruleLineDTOList);
    }

}
