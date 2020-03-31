package com.hand.hdsp.quality.app.service.impl;

import com.hand.hdsp.quality.api.dto.BatchPlanFieldDTO;
import com.hand.hdsp.quality.api.dto.BatchPlanFieldLineDTO;
import com.hand.hdsp.quality.api.dto.RuleDTO;
import com.hand.hdsp.quality.app.service.BatchPlanFieldLineService;
import com.hand.hdsp.quality.domain.entity.BatchPlanFieldLine;
import com.hand.hdsp.quality.domain.repository.BatchPlanFieldLineRepository;
import com.hand.hdsp.quality.infra.constant.RuleConstant;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>批数据方案-字段规则校验项表应用服务默认实现</p>
 *
 * @author feng.liu01@hand-china.com 2020-03-24 16:19:53
 */
@Service
public class BatchPlanFieldLineServiceImpl implements BatchPlanFieldLineService {

    private BatchPlanFieldLineRepository batchPlanFieldLineRepository;

    public BatchPlanFieldLineServiceImpl(BatchPlanFieldLineRepository batchPlanFieldLineRepository) {
        this.batchPlanFieldLineRepository = batchPlanFieldLineRepository;
    }

    @Override
    public List<BatchPlanFieldLine> list2(BatchPlanFieldDTO batchPlanFieldDTO) {
        return batchPlanFieldLineRepository.select(
                BatchPlanFieldLine.builder().planFieldId(batchPlanFieldDTO.getPlanFieldId()).build());
    }

    @Override
    public List<BatchPlanFieldLineDTO> list(BatchPlanFieldDTO batchPlanFieldDTO, RuleDTO ruleDTO) {
        List<BatchPlanFieldLine> batchPlanFieldLineList =
                batchPlanFieldLineRepository.select(
                        BatchPlanFieldLine.builder().planFieldId(batchPlanFieldDTO.getPlanFieldId()).build());
        List<String> checkItemList = new ArrayList<>();
        for (BatchPlanFieldLine batchPlanFieldLine : batchPlanFieldLineList) {
            checkItemList.add(batchPlanFieldLine.getCheckItem());
        }
        if (ruleDTO.getRuleModel() == null || ruleDTO.getRuleModel().equals(RuleConstant.RULE_MODEL_STANDARD)) {
            ruleDTO.setRuleModel(RuleConstant.RULE_MODEL_STANDARD);
            return batchPlanFieldLineRepository.list(checkItemList, ruleDTO.getRuleModel());
        } else if (ruleDTO.getRuleModel().equals(RuleConstant.RULE_MODEL_STREAMING)) {
            return batchPlanFieldLineRepository.list(checkItemList, ruleDTO.getRuleModel());
        }
        return null;
    }
}
