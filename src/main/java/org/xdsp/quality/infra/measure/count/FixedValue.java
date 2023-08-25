package org.xdsp.quality.infra.measure.count;

import org.xdsp.quality.domain.entity.BatchResultItem;
import org.xdsp.quality.infra.dataobject.MeasureParamDO;
import org.xdsp.quality.infra.measure.Count;
import org.xdsp.quality.infra.measure.CountType;
import org.xdsp.quality.infra.measure.MeasureUtil;

/**
 * <p>固定值</p>
 *
 * @author feng.liu01@hand-china.com 2020-03-24 16:19:53
 */
@CountType("FIXED_VALUE")
public class FixedValue implements Count {


    @Override
    public BatchResultItem count(MeasureParamDO param) {
        MeasureUtil.fixedCompare(param.getCompareWay(), param.getCountValue(), param.getWarningLevelList(), param.getBatchResultItem());
        return param.getBatchResultItem();
    }
}
