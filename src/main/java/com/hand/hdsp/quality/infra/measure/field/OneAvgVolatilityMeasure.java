package com.hand.hdsp.quality.infra.measure.field;

import com.fasterxml.jackson.core.type.TypeReference;
import com.hand.hdsp.quality.api.dto.BatchPlanFieldLineDTO;
import com.hand.hdsp.quality.api.dto.BatchResultRuleDTO;
import com.hand.hdsp.quality.api.dto.DatasourceDTO;
import com.hand.hdsp.quality.domain.entity.BatchPlanField;
import com.hand.hdsp.quality.domain.entity.PlanWarningLevel;
import com.hand.hdsp.quality.infra.dataobject.BatchResultRuleDO;
import com.hand.hdsp.quality.infra.dataobject.MeasureParamDO;
import com.hand.hdsp.quality.infra.feign.DatasourceFeign;
import com.hand.hdsp.quality.infra.mapper.BatchResultRuleMapper;
import com.hand.hdsp.quality.infra.measure.CheckItem;
import com.hand.hdsp.quality.infra.measure.Measure;
import com.hand.hdsp.quality.infra.measure.MeasureUtil;
import org.apache.commons.lang3.time.DateUtils;
import org.hzero.core.util.ResponseUtils;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * <p>平均值,1天波动率</p>
 *
 * @author feng.liu01@hand-china.com 2020-03-24 16:19:53
 */
@CheckItem("FIELD_ONE_AVG_VOLATILITY")
public class OneAvgVolatilityMeasure implements Measure {

    private static final String SQL = "select avg(%s) currentValue from %s";
    private final DatasourceFeign datasourceFeign;
    private final BatchResultRuleMapper batchResultRuleMapper;

    public OneAvgVolatilityMeasure(DatasourceFeign datasourceFeign, BatchResultRuleMapper batchResultRuleMapper) {
        this.datasourceFeign = datasourceFeign;
        this.batchResultRuleMapper = batchResultRuleMapper;
    }

    @Override
    public BatchResultRuleDTO check(MeasureParamDO param) {
        Long tenantId = param.getTenantId();
        DatasourceDTO datasourceDTO = param.getDatasourceDTO();
        BatchPlanField batchPlanField = param.getBatchPlanField();
        BatchPlanFieldLineDTO batchPlanFieldLineDTO = param.getBatchPlanFieldLineDTO();
        List<PlanWarningLevel> warningLevelList = param.getWarningLevelList();

        datasourceDTO.setSql(String.format(SQL, batchPlanField.getFieldName(), datasourceDTO.getTableName()));
        List<BatchResultRuleDTO> batchResultRuleDTOList = ResponseUtils.getResponse(datasourceFeign.execSql(tenantId, datasourceDTO), new TypeReference<List<BatchResultRuleDTO>>() {
        });
        String currentValue = batchResultRuleDTOList.get(0).getCurrentValue();

        BatchResultRuleDTO batchResultRuleDTO = new BatchResultRuleDTO();
        batchResultRuleDTO.setCurrentValue(currentValue);

        //查询基础值
        List<BatchResultRuleDO> baseList = batchResultRuleMapper.queryList(BatchResultRuleDO.builder()
                .planFieldLineId(batchPlanFieldLineDTO.getPlanFieldLineId())
                .measureDate(DateUtils.addDays(new Date(), -1))
                .build());
        if (baseList.isEmpty()) {
            return batchResultRuleDTO;
        }

        MeasureUtil.volatilityCompare(batchPlanFieldLineDTO.getCompareWay(),
                new BigDecimal(currentValue),
                new BigDecimal(baseList.get(0).getCurrentValue()),
                warningLevelList, batchResultRuleDTO);

        return batchResultRuleDTO;
    }
}
