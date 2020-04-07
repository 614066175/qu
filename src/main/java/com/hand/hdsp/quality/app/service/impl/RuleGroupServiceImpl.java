package com.hand.hdsp.quality.app.service.impl;

import com.hand.hdsp.quality.api.dto.RuleDTO;
import com.hand.hdsp.quality.api.dto.RuleGroupDTO;
import com.hand.hdsp.quality.app.service.RuleGroupService;
import com.hand.hdsp.quality.domain.entity.Rule;
import com.hand.hdsp.quality.domain.entity.RuleGroup;
import com.hand.hdsp.quality.domain.repository.RuleGroupRepository;
import com.hand.hdsp.quality.domain.repository.RuleRepository;
import com.hand.hdsp.quality.infra.constant.ErrorCode;
import io.choerodon.core.exception.CommonException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    private RuleGroupRepository ruleGroupRepository;
    private RuleRepository ruleRepository;

    public RuleGroupServiceImpl(RuleGroupRepository ruleGroupRepository, RuleRepository ruleRepository) {
        this.ruleGroupRepository = ruleGroupRepository;
        this.ruleRepository = ruleRepository;
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
}
