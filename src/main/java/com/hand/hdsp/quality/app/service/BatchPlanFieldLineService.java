package com.hand.hdsp.quality.app.service;

import com.hand.hdsp.quality.api.dto.BatchPlanFieldDTO;
import com.hand.hdsp.quality.api.dto.BatchPlanFieldLineDTO;
import com.hand.hdsp.quality.api.dto.RuleDTO;
import com.hand.hdsp.quality.domain.entity.BatchPlanFieldLine;

import java.util.List;

/**
 * <p>批数据方案-字段规则校验项表应用服务</p>
 *
 * @author feng.liu01@hand-china.com 2020-03-24 16:19:53
 */
public interface BatchPlanFieldLineService {

    /**
     * 可选字段级规则列表
     *
     * @param batchPlanFieldDTO
     * @param ruleDTO
     * @return
     */
    List<BatchPlanFieldLineDTO> list(BatchPlanFieldDTO batchPlanFieldDTO, RuleDTO ruleDTO);

    /**
     * 已选字段级规则列表
     *
     * @param batchPlanFieldDTO
     * @return
     */
    List<BatchPlanFieldLine> list2(BatchPlanFieldDTO batchPlanFieldDTO);
}
