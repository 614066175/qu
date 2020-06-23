package com.hand.hdsp.quality.infra.measure.count;

import com.hand.hdsp.quality.domain.entity.BatchResultItem;
import com.hand.hdsp.quality.infra.dataobject.BatchResultItemDO;
import com.hand.hdsp.quality.infra.dataobject.MeasureParamDO;
import com.hand.hdsp.quality.infra.mapper.BatchResultItemMapper;
import com.hand.hdsp.quality.infra.measure.Count;
import com.hand.hdsp.quality.infra.measure.CountType;
import com.hand.hdsp.quality.infra.measure.MeasureUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.time.DateUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import java.util.List;

/**
 * <p>7天平均值波动率</p>
 *
 * @author feng.liu01@hand-china.com 2020-03-24 16:19:53
 */
@CountType("SEVEN_AVG_VOLATILITY")
public class SevenAvgVolatility implements Count {
    private final BatchResultItemMapper batchResultItemMapper;

    public SevenAvgVolatility(BatchResultItemMapper batchResultItemMapper) {
        this.batchResultItemMapper = batchResultItemMapper;
    }

    @Override
    public BatchResultItem count(MeasureParamDO param) {

        //查询基础值
        List<BatchResultItemDO> resultList = batchResultItemMapper.queryList(BatchResultItemDO.builder()
                .conditionId(param.getConditionId())
                .ruleType(param.getBatchResultRuleDTO().getRuleType())
                .measureDateFrom(DateUtils.addDays(new Date(), -7))
                .build());

        if (CollectionUtils.isEmpty(resultList)) {
            return param.getBatchResultItem();
        }

        //求7天平均值
        BigDecimal sum = resultList.stream()
                .map(value -> new BigDecimal(value.getCurrentValue()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        //平均值
        BigDecimal base = sum.divide(BigDecimal.valueOf(resultList.size()), 5, RoundingMode.HALF_UP);

        MeasureUtil.volatilityCompare(param.getCompareWay(),
                new BigDecimal(param.getCountValue()),
                base,
                param.getWarningLevelList(), param.getBatchResultItem());
        return param.getBatchResultItem();
    }
}
