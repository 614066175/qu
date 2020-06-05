package com.hand.hdsp.quality.infra.measure.table;

import com.fasterxml.jackson.core.type.TypeReference;
import com.hand.hdsp.quality.api.dto.BatchPlanTableLineDTO;
import com.hand.hdsp.quality.api.dto.BatchResultRuleDTO;
import com.hand.hdsp.quality.api.dto.DatasourceDTO;
import com.hand.hdsp.quality.infra.constant.ErrorCode;
import com.hand.hdsp.quality.infra.constant.PlanConstant;
import com.hand.hdsp.quality.infra.dataobject.BatchResultRuleDO;
import com.hand.hdsp.quality.infra.dataobject.MeasureParamDO;
import com.hand.hdsp.quality.infra.feign.DatasourceFeign;
import com.hand.hdsp.quality.infra.mapper.BatchResultRuleMapper;
import com.hand.hdsp.quality.infra.measure.CheckItem;
import com.hand.hdsp.quality.infra.measure.Measure;
import org.apache.commons.lang3.time.DateUtils;
import org.hzero.core.exception.MessageException;
import org.hzero.core.message.MessageAccessor;
import org.hzero.core.util.ResponseUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * <p>自定义SQL：7天平均值波动率</p>
 *
 * @author feng.liu01@hand-china.com 2020-03-24 16:19:53
 */
@CheckItem("CUSTOM_SEVEN_VOLATILITY")
public class CustomSevenVolatilityMeasure implements Measure {

    private final DatasourceFeign datasourceFeign;
    private final BatchResultRuleMapper batchResultRuleMapper;

    public CustomSevenVolatilityMeasure(DatasourceFeign datasourceFeign, BatchResultRuleMapper batchResultRuleMapper) {
        this.datasourceFeign = datasourceFeign;
        this.batchResultRuleMapper = batchResultRuleMapper;
    }

    @Override
    public BatchResultRuleDTO check(MeasureParamDO param) {
        Long tenantId = param.getTenantId();
        DatasourceDTO datasourceDTO = param.getDatasourceDTO();
        BatchPlanTableLineDTO batchPlanTableLineDTO = param.getBatchPlanTableLineDTO();


        datasourceDTO.setSql(batchPlanTableLineDTO.getCustomSql());
        List<HashMap<String, String>> response = ResponseUtils.getResponse(datasourceFeign.execSql(tenantId, datasourceDTO), new TypeReference<List<HashMap<String, String>>>() {
        });
        if (response.size() != 1 || response.get(0).size() != 1) {
            throw new MessageException(MessageAccessor.getMessage(ErrorCode.CUSTOM_SQL_ONE_VALUE).getDesc());
        }

        String currentValue = (String) response.get(0).values().toArray()[0];

        BatchResultRuleDTO batchResultRuleDTO = new BatchResultRuleDTO();
        batchResultRuleDTO.setCurrentValue(currentValue);

        // 查询七天前的表行数
        List<BatchResultRuleDO> resultList = batchResultRuleMapper.queryList(BatchResultRuleDO.builder()
                .ruleType(PlanConstant.ResultRuleType.TABLE)
                .ruleId(batchPlanTableLineDTO.getPlanLineId())
                .measureDate(DateUtils.addDays(new Date(), -7))
                .build());

        //求7天平均值
        long sum = 0;
        long count = 0;
        for (BatchResultRuleDO batchResultRuleDO : resultList) {
            if (batchResultRuleDO.getActualValue() != null) {
                sum += Long.parseLong(batchResultRuleDO.getActualValue());
                count++;
            }
        }

        if (count == 0) {
            return batchResultRuleDTO;
        }

        BigDecimal base = BigDecimal.valueOf(sum).divide(BigDecimal.valueOf(count), 5, RoundingMode.HALF_UP);

//        MeasureUtil.volatilityCompare(batchPlanTableLineDTO.getCompareWay(),
//                new BigDecimal(currentValue),
//                base,
//                warningLevelList, batchResultRuleDTO);
        return batchResultRuleDTO;
    }
}
