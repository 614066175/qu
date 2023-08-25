package org.xdsp.quality.infra.measure.count;

import org.apache.commons.lang3.time.DateUtils;
import org.xdsp.quality.domain.entity.BatchResultItem;
import org.xdsp.quality.infra.dataobject.BatchResultItemDO;
import org.xdsp.quality.infra.dataobject.MeasureParamDO;
import org.xdsp.quality.infra.mapper.BatchResultItemMapper;
import org.xdsp.quality.infra.measure.Count;
import org.xdsp.quality.infra.measure.CountType;
import org.xdsp.quality.infra.measure.MeasureUtil;

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
            //清空实际值
            batchResultItem.setActualValue(null);
            return batchResultItem;
        }

        double countValue = Double.parseDouble(param.getCountValue()) - Double.parseDouble(resultList.get(0).getCurrentValue());
        param.setCountValue(countValue + "");
        batchResultItem.setActualValue(countValue + "");

        MeasureUtil.fixedCompare(param.getCompareWay(), countValue + "", param.getWarningLevelList(), batchResultItem);
        return batchResultItem;
    }
}
