package com.hand.hdsp.quantity.app.service.impl;

import com.hand.hdsp.quantity.api.dto.RuleGroupDTO;
import com.hand.hdsp.quantity.app.service.RuleGroupService;
import com.hand.hdsp.quantity.domain.entity.RuleGroup;
import com.hand.hdsp.quantity.domain.repository.RuleGroupRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 规则分组表应用服务默认实现
 *
 * @author wuzhong26857
 * @version 1.0
 * @date 2020/3/23 13:36
 */
@Transactional(rollbackFor = Exception.class)
@Service
@Slf4j
public class RuleGroupServiceImpl implements RuleGroupService {

    @Autowired
    private RuleGroupService ruleGroupService;

    @Autowired
    private RuleGroupRepository ruleGroupRepository;

    @Override
    public void create(RuleGroupDTO ruleGroupDTO) {
        RuleGroup ruleGroup = new RuleGroup();
        //把ruleGroupDTO的值给ruleGroup
        BeanUtils.copyProperties(ruleGroupDTO, ruleGroup);
        //编码重复校验
        ruleGroup.validCodeRepeat(ruleGroupRepository);
        ruleGroupRepository.insertSelective(ruleGroup);
    }
}
