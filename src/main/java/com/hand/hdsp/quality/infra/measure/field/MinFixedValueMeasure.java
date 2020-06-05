package com.hand.hdsp.quality.infra.measure.field;

import com.fasterxml.jackson.core.type.TypeReference;
import com.hand.hdsp.quality.api.dto.BatchPlanFieldLineDTO;
import com.hand.hdsp.quality.api.dto.BatchResultRuleDTO;
import com.hand.hdsp.quality.api.dto.DatasourceDTO;
import com.hand.hdsp.quality.domain.entity.BatchPlanField;
import com.hand.hdsp.quality.infra.dataobject.MeasureParamDO;
import com.hand.hdsp.quality.infra.feign.DatasourceFeign;
import com.hand.hdsp.quality.infra.measure.CheckItem;
import com.hand.hdsp.quality.infra.measure.Measure;
import org.hzero.core.util.ResponseUtils;

import java.util.List;

/**
 * <p>最小值,固定值</p>
 *
 * @author feng.liu01@hand-china.com 2020-03-24 16:19:53
 */
@CheckItem("FIELD_MIN_FIXED_VALUE")
public class MinFixedValueMeasure implements Measure {

    private static final String SQL = "select min(%s) actualValue from %s";
    private final DatasourceFeign datasourceFeign;

    public MinFixedValueMeasure(DatasourceFeign datasourceFeign) {
        this.datasourceFeign = datasourceFeign;
    }

    @Override
    public BatchResultRuleDTO check(MeasureParamDO param) {
        Long tenantId = param.getTenantId();
        DatasourceDTO datasourceDTO = param.getDatasourceDTO();
        BatchPlanField batchPlanField = param.getBatchPlanField();
        BatchPlanFieldLineDTO batchPlanFieldLineDTO = param.getBatchPlanFieldLineDTO();


        datasourceDTO.setSql(String.format(SQL, batchPlanFieldLineDTO.getFieldName(), datasourceDTO.getTableName()));
        List<BatchResultRuleDTO> batchResultRuleDTOList = ResponseUtils.getResponse(datasourceFeign.execSql(tenantId, datasourceDTO), new TypeReference<List<BatchResultRuleDTO>>() {
        });
        BatchResultRuleDTO batchResultRuleDTO = batchResultRuleDTOList.get(0);
//        batchResultRuleDTO.setExpectedValue(batchPlanFieldLineDTO.getExpectedValue());
//
//        double actualValue = Double.parseDouble(batchResultRuleDTO.getActualValue());
//        double expectedValue = Double.parseDouble(batchPlanFieldLineDTO.getExpectedValue());
//        MeasureUtil.fixedCompare(batchPlanFieldLineDTO.getCompareWay(), actualValue, expectedValue, warningLevelList, batchResultRuleDTO);

        return batchResultRuleDTO;
    }
}
