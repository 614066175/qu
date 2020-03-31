package com.hand.hdsp.quality.infra.measure.table;

import com.hand.hdsp.quality.api.dto.BatchPlanTableLineDTO;
import com.hand.hdsp.quality.api.dto.BatchResultRuleDTO;
import com.hand.hdsp.quality.domain.entity.PlanWarningLevel;
import com.hand.hdsp.quality.infra.dataobject.MeasureParamDO;
import com.hand.hdsp.quality.infra.measure.CheckItem;
import com.hand.hdsp.quality.infra.measure.Measure;
import com.hand.hdsp.quality.infra.measure.MeasureUtil;

import java.util.List;

/**
 * <p>表行数:固定值</p>
 *
 * @author feng.liu01@hand-china.com 2020-03-24 16:19:53
 */
@CheckItem("TABLE_LINE_FIXED_VALUE")
public class LineFixedValueMeasure implements Measure {

    @Override
    public BatchResultRuleDTO check(MeasureParamDO param) {
        BatchPlanTableLineDTO batchPlanTableLineDTO = param.getBatchPlanTableLineDTO();
        List<PlanWarningLevel> warningLevelList = param.getWarningLevelList();
        long actualValue = param.getBatchResultBase().getDataCount();

        BatchResultRuleDTO batchResultRuleDTO = new BatchResultRuleDTO();
        batchResultRuleDTO.setExpectedValue(batchPlanTableLineDTO.getExpectedValue());

        long expectedValue = Long.parseLong(batchPlanTableLineDTO.getExpectedValue());
        boolean result = MeasureUtil.fixedCompare(batchPlanTableLineDTO.getCompareWay(), actualValue, expectedValue);
        if (result) {
            batchResultRuleDTO.setWarningLevel(warningLevelList.get(0).getWarningLevel());
        }

        batchResultRuleDTO.setActualValue(actualValue + "");
        return batchResultRuleDTO;
    }
}
