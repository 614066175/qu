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
 * <p>1、7、30天波动率</p>
 *
 * @author feng.liu01@hand-china.com 2020-03-24 16:19:53
 */
@CountType("OST_VOLATILITY")
public class OstVolatility implements Count {
    private final BatchResultItemMapper batchResultItemMapper;

    public OstVolatility(BatchResultItemMapper batchResultItemMapper) {
        this.batchResultItemMapper = batchResultItemMapper;
    }

    @Override
    public BatchResultItem count(MeasureParamDO param) {
        BatchResultItem batchResultItem = param.getBatchResultItem();

        //查询基础值(1天)
        BatchResultItem oneResult = countOne(param, -1);

        //查询基础值(7天)
        BatchResultItem sevenResult = countOne(param, -7);

        //查询基础值(30天)
        BatchResultItem thirtyResult = countOne(param, -7);

        //最终结果
        batchResultItem.setWaveRate(oneResult.getWaveRate() + "、" + sevenResult.getWaveRate() + "、" + thirtyResult.getWaveRate());
        if (oneResult.getWarningLevel() != null) {
            batchResultItem.setWarningLevel(oneResult.getWarningLevel());
            batchResultItem.setExceptionInfo(oneResult.getExceptionInfo());
        } else if (sevenResult.getWarningLevel() != null) {
            batchResultItem.setWarningLevel(sevenResult.getWarningLevel());
            batchResultItem.setExceptionInfo(sevenResult.getExceptionInfo());
        } else {
            batchResultItem.setWarningLevel(thirtyResult.getWarningLevel());
            batchResultItem.setExceptionInfo(thirtyResult.getExceptionInfo());
        }

        return batchResultItem;
    }

    /**
     * 计算1、7、30天的波动率，day参数为时间
     *
     * @param param 参数
     * @param day   天数
     * @return
     */
    private BatchResultItem countOne(MeasureParamDO param, int day) {
        List<BatchResultItemDO> baseList = batchResultItemMapper.queryList(BatchResultItemDO.builder()
                .conditionId(param.getConditionId())
                .measureDate(DateUtils.addDays(new Date(), day))
                .build());
        BatchResultItem result = new BatchResultItem();
        if (baseList.isEmpty()) {
            result.setWaveRate("0%");
        } else {
            MeasureUtil.volatilityCompare(param.getCompareWay(),
                    new BigDecimal(param.getCountValue()),
                    new BigDecimal(baseList.get(0).getCurrentValue()),
                    param.getWarningLevelList(), result);
        }
        return result;
    }
}
