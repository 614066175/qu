package org.xdsp.quality.infra.measure.count;

import org.apache.commons.lang3.time.DateUtils;
import org.xdsp.quality.domain.entity.BatchResultItem;
import org.xdsp.quality.infra.dataobject.BatchResultItemDO;
import org.xdsp.quality.infra.dataobject.MeasureParamDO;
import org.xdsp.quality.infra.mapper.BatchResultItemMapper;
import org.xdsp.quality.infra.measure.Count;
import org.xdsp.quality.infra.measure.CountType;
import org.xdsp.quality.infra.measure.MeasureUtil;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * <p>本月1号,波动率</p>
 *
 * @author feng.liu01@hand-china.com 2020-03-24 16:19:53
 */
@CountType("MONTH_FIRST_VOLATILITY")
public class MonthFirstVolatility implements Count {
    private final BatchResultItemMapper batchResultItemMapper;

    public MonthFirstVolatility(BatchResultItemMapper batchResultItemMapper) {
        this.batchResultItemMapper = batchResultItemMapper;
    }

    @Override
    public BatchResultItem count(MeasureParamDO param) {

        //查询基础值
        List<BatchResultItemDO> baseList = batchResultItemMapper.queryList(BatchResultItemDO.builder()
                .conditionId(param.getConditionId())
                .ruleType(param.getBatchResultRuleDTO().getRuleType())
                .measureDate(DateUtils.setDays(new Date(), 1))
                .build());
        if (baseList.isEmpty()) {
            //清空实际值
            param.getBatchResultItem().setActualValue(null);
            return param.getBatchResultItem();
        }

        MeasureUtil.volatilityCompare(param.getCompareWay(),
                new BigDecimal(param.getCountValue()),
                new BigDecimal(baseList.get(0).getCurrentValue()),
                param.getWarningLevelList(), param.getBatchResultItem());
        return param.getBatchResultItem();
    }
}
