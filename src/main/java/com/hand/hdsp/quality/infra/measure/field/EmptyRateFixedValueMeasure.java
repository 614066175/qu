package com.hand.hdsp.quality.infra.measure.field;

import com.fasterxml.jackson.core.type.TypeReference;
import com.hand.hdsp.quality.api.dto.BatchPlanFieldLineDTO;
import com.hand.hdsp.quality.api.dto.BatchResultRuleDTO;
import com.hand.hdsp.quality.api.dto.DatasourceDTO;
import com.hand.hdsp.quality.domain.entity.BatchPlanField;
import com.hand.hdsp.quality.domain.entity.PlanWarningLevel;
import com.hand.hdsp.quality.infra.dataobject.MeasureParamDO;
import com.hand.hdsp.quality.infra.feign.DatasourceFeign;
import com.hand.hdsp.quality.infra.measure.CheckItem;
import com.hand.hdsp.quality.infra.measure.Measure;
import com.hand.hdsp.quality.infra.measure.MeasureUtil;
import org.hzero.core.util.ResponseUtils;

import java.util.List;

/**
 * <p>空值个数/总行数,固定值</p>
 *
 * @author feng.liu01@hand-china.com 2020-03-24 16:19:53
 */
@CheckItem("FIELD_EMPTY_RATE_FIXED_VALUE")
public class EmptyRateFixedValueMeasure implements Measure {

    private static final String SQL = "SELECT COUNT(*) actualValue FROM %s WHERE %s is null";
    private final DatasourceFeign datasourceFeign;

    public EmptyRateFixedValueMeasure(DatasourceFeign datasourceFeign) {
        this.datasourceFeign = datasourceFeign;
    }

    @Override
    public BatchResultRuleDTO check(MeasureParamDO param) {
        Long tenantId = param.getTenantId();
        BatchPlanField batchPlanField = param.getBatchPlanField();
        BatchPlanFieldLineDTO batchPlanFieldLineDTO = param.getBatchPlanFieldLineDTO();
        DatasourceDTO datasourceDTO = param.getDatasourceDTO();
        List<PlanWarningLevel> warningLevelList = param.getWarningLevelList();
        long dataCount = param.getBatchResultBase().getDataCount();

        datasourceDTO.setSql(String.format(SQL, datasourceDTO.getTableName(), batchPlanField.getFieldName()));
        List<BatchResultRuleDTO> resultList = ResponseUtils.getResponse(datasourceFeign.execSql(tenantId, datasourceDTO), new TypeReference<List<BatchResultRuleDTO>>() {
        });
        long baseValue = Long.parseLong(resultList.get(0).getActualValue());
        long actualValue = MeasureUtil.divide(baseValue, dataCount);

        BatchResultRuleDTO batchResultRuleDTO = new BatchResultRuleDTO();
        batchResultRuleDTO.setExpectedValue(batchPlanFieldLineDTO.getExpectedValue());

        long expectedValue = Long.parseLong(batchPlanFieldLineDTO.getExpectedValue());
        MeasureUtil.fixedCompare(batchPlanFieldLineDTO.getCompareWay(), actualValue, expectedValue, warningLevelList, batchResultRuleDTO);

        batchResultRuleDTO.setActualValue(resultList.get(0).getActualValue());
        return batchResultRuleDTO;
    }
}
