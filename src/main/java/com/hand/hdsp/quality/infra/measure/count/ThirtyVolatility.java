package com.hand.hdsp.quality.infra.measure.count;

import com.hand.hdsp.quality.domain.entity.BatchResultItem;
import com.hand.hdsp.quality.infra.dataobject.BatchResultItemDO;
import com.hand.hdsp.quality.infra.dataobject.MeasureParamDO;
import com.hand.hdsp.quality.infra.mapper.BatchResultItemMapper;
import com.hand.hdsp.quality.infra.measure.Count;
import com.hand.hdsp.quality.infra.measure.CountType;
import com.hand.hdsp.quality.infra.measure.MeasureUtil;
import org.apache.commons.lang3.time.DateUtils;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * <p>30天波动率</p>
 *
 * @author feng.liu01@hand-china.com 2020-03-24 16:19:53
 */
@CountType("THIRTY_VOLATILITY")
public class ThirtyVolatility implements Count {
    private final BatchResultItemMapper batchResultItemMapper;

    public ThirtyVolatility(BatchResultItemMapper batchResultItemMapper) {
        this.batchResultItemMapper = batchResultItemMapper;
    }

    @Override
    public BatchResultItem count(MeasureParamDO param) {

        //查询基础值
        List<BatchResultItemDO> baseList = batchResultItemMapper.queryList(BatchResultItemDO.builder()
                .conditionId(param.getConditionId())
                .ruleType(param.getBatchResultRuleDTO().getRuleType())
                .measureDate(DateUtils.addDays(new Date(), -30))
                .build());
        if (baseList.isEmpty()) {
            return param.getBatchResultItem();
        }

        MeasureUtil.volatilityCompare(param.getCompareWay(),
                new BigDecimal(param.getCountValue()),
                new BigDecimal(baseList.get(0).getCurrentValue()),
                param.getWarningLevelList(), param.getBatchResultItem());
        return param.getBatchResultItem();
    }
}
