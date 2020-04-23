package com.hand.hdsp.quality.app.service.impl;

import com.hand.hdsp.quality.api.dto.RuleWarningLevelDTO;
import com.hand.hdsp.quality.app.service.RuleWarningLevelService;
import com.hand.hdsp.quality.domain.repository.RuleWarningLevelRepository;
import com.hand.hdsp.quality.infra.constant.ErrorCode;
import io.choerodon.core.exception.CommonException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * <p>规则告警等级表应用服务默认实现</p>
 *
 * @author feng.liu01@hand-china.com 2020-03-24 09:31:46
 */
@Service
public class RuleWarningLevelServiceImpl implements RuleWarningLevelService {

    private final RuleWarningLevelRepository ruleWarningLevelRepository;

    public RuleWarningLevelServiceImpl(RuleWarningLevelRepository ruleWarningLevelRepository) {
        this.ruleWarningLevelRepository = ruleWarningLevelRepository;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertAndValid(RuleWarningLevelDTO ruleWarningLevelDTO) {
        if (ruleWarningLevelRepository.judgeOverlap(ruleWarningLevelDTO) == 0) {
            throw new CommonException(ErrorCode.WARNING_LEVEL_OVERLAP);
        }
        return ruleWarningLevelRepository.insertDTOSelective(ruleWarningLevelDTO);
    }

    @Override
    public int updateAndValid(RuleWarningLevelDTO ruleWarningLevelDTO, Long tenantId) {
        if (ruleWarningLevelRepository.judgeOverlap(ruleWarningLevelDTO) == 0) {
            throw new CommonException(ErrorCode.WARNING_LEVEL_OVERLAP);
        }
        return ruleWarningLevelRepository.updateDTOWhereTenant(ruleWarningLevelDTO, tenantId);
    }
}
