package com.hand.hdsp.quality.infra.measure.count;

import com.hand.hdsp.quality.domain.entity.BatchResultItem;
import com.hand.hdsp.quality.infra.dataobject.BatchResultItemDO;
import com.hand.hdsp.quality.infra.dataobject.MeasureParamDO;
import com.hand.hdsp.quality.infra.mapper.BatchResultItemMapper;
import com.hand.hdsp.quality.infra.measure.Count;
import com.hand.hdsp.quality.infra.measure.CountType;
import com.hand.hdsp.quality.infra.measure.MeasureUtil;
import org.apache.commons.lang3.time.DateUtils;

import java.util.Date;
import java.util.List;

/**
 * <p>1天差值</p>
 *
 * @author feng.liu01@hand-china.com 2020-03-24 16:19:53
 */
@CountType("ONE_DIFF_VALUE")
public class OneDiffValue implements Count {
    private final BatchResultItemMapper batchResultItemMapper;

    public OneDiffValue(BatchResultItemMapper batchResultItemMapper) {
        this.batchResultItemMapper = batchResultItemMapper;
    }

    @Override
    public BatchResultItem count(MeasureParamDO param) {
        //查询基础值
        List<BatchResultItemDO> resultList = batchResultItemMapper.queryList(BatchResultItemDO.builder()
                .conditionId(param.getConditionId())
                .ruleType(param.getBatchResultRuleDTO().getRuleType())
                .measureDate(DateUtils.addDays(new Date(), -1))
                .build());
        BatchResultItem batchResultItem = param.getBatchResultItem();
        if (resultList.isEmpty()) {
            return batchResultItem;
        }

        double countValue = Double.parseDouble(param.getCountValue()) - Double.parseDouble(resultList.get(0).getCurrentValue());
        param.setCountValue(countValue + "");
        batchResultItem.setActualValue(countValue + "");

        MeasureUtil.fixedCompare(param.getCompareWay(), countValue + "", param.getWarningLevelList(), batchResultItem);
        return batchResultItem;
    }
}
