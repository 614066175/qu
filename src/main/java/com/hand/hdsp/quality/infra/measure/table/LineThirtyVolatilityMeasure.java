package com.hand.hdsp.quality.infra.measure.table;

import com.hand.hdsp.quality.api.dto.BatchPlanTableLineDTO;
import com.hand.hdsp.quality.api.dto.BatchResultRuleDTO;
import com.hand.hdsp.quality.infra.dataobject.BatchResultBaseDO;
import com.hand.hdsp.quality.infra.dataobject.MeasureParamDO;
import com.hand.hdsp.quality.infra.mapper.BatchResultBaseMapper;
import com.hand.hdsp.quality.infra.measure.CheckItem;
import com.hand.hdsp.quality.infra.measure.Measure;
import org.apache.commons.lang3.time.DateUtils;

import java.util.Date;
import java.util.List;

/**
 * <p>表行数:30天波动率</p>
 *
 * @author feng.liu01@hand-china.com 2020-03-24 16:19:53
 */
@CheckItem("TABLE_LINE_THIRTY_VOLATILITY")
public class LineThirtyVolatilityMeasure implements Measure {

    private final BatchResultBaseMapper batchResultBaseMapper;

    public LineThirtyVolatilityMeasure(BatchResultBaseMapper batchResultBaseMapper) {
        this.batchResultBaseMapper = batchResultBaseMapper;
    }

    @Override
    public BatchResultRuleDTO check(MeasureParamDO param) {
        BatchPlanTableLineDTO batchPlanTableLineDTO = param.getBatchPlanTableLineDTO();

        long sample = param.getBatchResultBase().getDataCount();

        BatchResultRuleDTO batchResultRuleDTO = new BatchResultRuleDTO();
//        batchResultRuleDTO.setExpectedValue(batchPlanTableLineDTO.getExpectedValue());
        // 查询一天前的表行数
        List<BatchResultBaseDO> resultList = batchResultBaseMapper.queryList(BatchResultBaseDO.builder()
                .planBaseId(param.getBatchResultBase().getPlanBaseId())
                .measureDate(DateUtils.addDays(new Date(), -30))
                .build());
        if (resultList.isEmpty()) {
            return batchResultRuleDTO;
        }

        long base = resultList.get(0).getDataCount();
//        MeasureUtil.volatilityCompare(batchPlanTableLineDTO.getCompareWay(), BigDecimal.valueOf(sample), BigDecimal.valueOf(base),
//                warningLevelList, batchResultRuleDTO);
        return batchResultRuleDTO;
    }
}
