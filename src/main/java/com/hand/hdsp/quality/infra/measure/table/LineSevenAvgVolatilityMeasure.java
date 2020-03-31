package com.hand.hdsp.quality.infra.measure.table;

import com.hand.hdsp.quality.api.dto.BatchPlanTableLineDTO;
import com.hand.hdsp.quality.api.dto.BatchResultRuleDTO;
import com.hand.hdsp.quality.domain.entity.PlanWarningLevel;
import com.hand.hdsp.quality.infra.dataobject.BatchResultBaseDO;
import com.hand.hdsp.quality.infra.dataobject.MeasureParamDO;
import com.hand.hdsp.quality.infra.mapper.BatchResultBaseMapper;
import com.hand.hdsp.quality.infra.measure.CheckItem;
import com.hand.hdsp.quality.infra.measure.Measure;
import com.hand.hdsp.quality.infra.measure.MeasureUtil;
import org.apache.commons.lang3.time.DateUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import java.util.List;

/**
 * <p>表行数:7天平均值波动率</p>
 *
 * @author feng.liu01@hand-china.com 2020-03-24 16:19:53
 */
@CheckItem("TABLE_LINE_SEVEN_AVG_VOLATILITY")
public class LineSevenAvgVolatilityMeasure implements Measure {

    private final BatchResultBaseMapper batchResultBaseMapper;

    public LineSevenAvgVolatilityMeasure(BatchResultBaseMapper batchResultBaseMapper) {
        this.batchResultBaseMapper = batchResultBaseMapper;
    }

    @Override
    public BatchResultRuleDTO check(MeasureParamDO param) {
        BatchPlanTableLineDTO batchPlanTableLineDTO = param.getBatchPlanTableLineDTO();
        List<PlanWarningLevel> warningLevelList = param.getWarningLevelList();
        long sample = param.getBatchResultBase().getDataCount();

        BatchResultRuleDTO batchResultRuleDTO = new BatchResultRuleDTO();
        batchResultRuleDTO.setExpectedValue(batchPlanTableLineDTO.getExpectedValue());
        // 查询一天前的表行数
        List<BatchResultBaseDO> resultList = batchResultBaseMapper.queryList(BatchResultBaseDO.builder()
                .planBaseId(param.getBatchResultBase().getPlanBaseId())
                .measureDateFrom(DateUtils.addDays(new Date(), -7))
                .build());

        //求7天平均值
        long sum = 0;
        long count = 0;
        for (BatchResultBaseDO batchResultBaseDO : resultList) {
            if (batchResultBaseDO.getDataCount() != null) {
                sum += batchResultBaseDO.getDataCount();
                count++;
            }
        }

        if (count == 0) {
            return batchResultRuleDTO;
        }

        BigDecimal base = BigDecimal.valueOf(sum).divide(BigDecimal.valueOf(count), 5, RoundingMode.HALF_UP);

        MeasureUtil.volatilityCompare(batchPlanTableLineDTO.getCompareWay(), BigDecimal.valueOf(sample), base,
                warningLevelList, batchResultRuleDTO);
        return batchResultRuleDTO;
    }
}
