package org.xdsp.quality.infra.measure.count;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.xdsp.quality.domain.entity.BatchResultItem;
import org.xdsp.quality.infra.dataobject.BatchResultItemDO;
import org.xdsp.quality.infra.dataobject.MeasureParamDO;
import org.xdsp.quality.infra.mapper.BatchResultItemMapper;
import org.xdsp.quality.infra.measure.Count;
import org.xdsp.quality.infra.measure.CountType;
import org.xdsp.quality.infra.measure.MeasureUtil;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import java.util.List;

/**
 * <p>30天平均值波动率</p>
 *
 * @author feng.liu01@hand-china.com 2020-03-24 16:19:53
 */
@CountType("THIRTY_AVG_VOLATILITY")
public class ThirtyAvgVolatility implements Count {
    private final BatchResultItemMapper batchResultItemMapper;

    public ThirtyAvgVolatility(BatchResultItemMapper batchResultItemMapper) {
        this.batchResultItemMapper = batchResultItemMapper;
    }

    @Override
    public BatchResultItem count(MeasureParamDO param) {

        //查询基础值
        List<BatchResultItemDO> resultList = batchResultItemMapper.queryList(BatchResultItemDO.builder()
                .conditionId(param.getConditionId())
                .ruleType(param.getBatchResultRuleDTO().getRuleType())
                .measureDateFrom(DateUtils.addDays(new Date(), -30))
                .build());

        if (CollectionUtils.isEmpty(resultList)) {
            //清空实际值
            param.getBatchResultItem().setActualValue(null);
            return param.getBatchResultItem();
        }

        //求30天平均值
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
