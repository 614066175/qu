package com.hand.hdsp.quality.app.service.impl;

import com.hand.hdsp.core.util.ProjectHelper;
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
import org.hzero.boot.platform.lov.annotation.ProcessLovValue;
import org.hzero.export.vo.ExportParam;
import org.hzero.mybatis.domian.Condition;
import org.hzero.mybatis.util.Sqls;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

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
        List<RuleGroupDTO> ruleGroupList = ruleGroupRepository.selectDTO(RuleGroup.FIELD_PARENT_GROUP_ID, ruleGroupDTO.getGroupId());
        List<RuleDTO> ruleDTOList = ruleRepository.selectDTO(Rule.FIELD_GROUP_ID, ruleGroupDTO.getGroupId());
        if (!ruleGroupList.isEmpty() || !ruleDTOList.isEmpty()) {
            throw new CommonException(ErrorCode.CAN_NOT_DELETE);
        }
        return ruleGroupRepository.deleteByPrimaryKey(ruleGroupDTO);
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
        List<RuleGroupDTO> ruleGroupDTOList = ruleGroupRepository.selectDTOByCondition(Condition.builder(RuleGroup.class)
                .andWhere(Sqls.custom()
                        .andEqualTo(RuleGroup.FIELD_GROUP_ID, dto.getGroupId(), true)
                        .andIn(RuleGroup.FIELD_TENANT_ID, Arrays.asList(0, dto.getTenantId()))
                        .andEqualTo(RuleGroup.FIELD_PROJECT_ID, dto.getProjectId()))
                .build());
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
}
