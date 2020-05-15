package com.hand.hdsp.quality.infra.measure.table;

import com.fasterxml.jackson.core.type.TypeReference;
import com.hand.hdsp.quality.api.dto.BatchPlanTableLineDTO;
import com.hand.hdsp.quality.api.dto.BatchResultRuleDTO;
import com.hand.hdsp.quality.api.dto.DatasourceDTO;
import com.hand.hdsp.quality.domain.entity.PlanWarningLevel;
import com.hand.hdsp.quality.infra.constant.ErrorCode;
import com.hand.hdsp.quality.infra.constant.PlanConstant;
import com.hand.hdsp.quality.infra.dataobject.BatchResultRuleDO;
import com.hand.hdsp.quality.infra.dataobject.MeasureParamDO;
import com.hand.hdsp.quality.infra.feign.DatasourceFeign;
import com.hand.hdsp.quality.infra.mapper.BatchResultRuleMapper;
import com.hand.hdsp.quality.infra.measure.CheckItem;
import com.hand.hdsp.quality.infra.measure.Measure;
import com.hand.hdsp.quality.infra.measure.MeasureUtil;
import org.apache.commons.lang3.time.DateUtils;
import org.hzero.core.exception.MessageException;
import org.hzero.core.message.MessageAccessor;
import org.hzero.core.util.ResponseUtils;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * <p>自定义SQL：1天波动率</p>
 *
 * @author feng.liu01@hand-china.com 2020-03-24 16:19:53
 */
@CheckItem("CUSTOM_ONE_VOLATILITY")
public class CustomOneVolatilityMeasure implements Measure {

    private final DatasourceFeign datasourceFeign;
    private final BatchResultRuleMapper batchResultRuleMapper;

    public CustomOneVolatilityMeasure(DatasourceFeign datasourceFeign, BatchResultRuleMapper batchResultRuleMapper) {
        this.datasourceFeign = datasourceFeign;
        this.batchResultRuleMapper = batchResultRuleMapper;
    }

    @Override
    public BatchResultRuleDTO check(MeasureParamDO param) {
        Long tenantId = param.getTenantId();
        DatasourceDTO datasourceDTO = param.getDatasourceDTO();
        BatchPlanTableLineDTO batchPlanTableLineDTO = param.getBatchPlanTableLineDTO();
        List<PlanWarningLevel> warningLevelList = param.getWarningLevelList();

        datasourceDTO.setSql(batchPlanTableLineDTO.getCustomSql());
        List<HashMap<String, String>> response = ResponseUtils.getResponse(datasourceFeign.execSql(tenantId, datasourceDTO), new TypeReference<List<HashMap<String, String>>>() {
        });
        if (response.size() != 1 || response.get(0).size() != 1) {
            throw new MessageException(MessageAccessor.getMessage(ErrorCode.CUSTOM_SQL_ONE_VALUE).getDesc());
        }

        String currentValue = (String) response.get(0).values().toArray()[0];

        BatchResultRuleDTO batchResultRuleDTO = new BatchResultRuleDTO();
        batchResultRuleDTO.setCurrentValue(currentValue);

        //查询基础值
        List<BatchResultRuleDO> baseList = batchResultRuleMapper.queryList(BatchResultRuleDO.builder()
                .ruleType(PlanConstant.ResultRuleType.TABLE)
                .ruleId(batchPlanTableLineDTO.getPlanTableLineId())
                .measureDate(DateUtils.addDays(new Date(), -1))
                .build());
        if (baseList.isEmpty()) {
            return batchResultRuleDTO;
        }

        MeasureUtil.volatilityCompare(batchPlanTableLineDTO.getCompareWay(),
                new BigDecimal(currentValue),
                new BigDecimal(baseList.get(0).getCurrentValue()),
                warningLevelList, batchResultRuleDTO);
        return batchResultRuleDTO;
    }
}
