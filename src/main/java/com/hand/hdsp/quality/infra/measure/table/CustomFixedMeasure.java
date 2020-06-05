package com.hand.hdsp.quality.infra.measure.table;

import com.fasterxml.jackson.core.type.TypeReference;
import com.hand.hdsp.quality.api.dto.BatchPlanTableLineDTO;
import com.hand.hdsp.quality.api.dto.BatchResultRuleDTO;
import com.hand.hdsp.quality.api.dto.DatasourceDTO;
import com.hand.hdsp.quality.infra.constant.ErrorCode;
import com.hand.hdsp.quality.infra.dataobject.MeasureParamDO;
import com.hand.hdsp.quality.infra.feign.DatasourceFeign;
import com.hand.hdsp.quality.infra.measure.CheckItem;
import com.hand.hdsp.quality.infra.measure.Measure;
import org.hzero.core.exception.MessageException;
import org.hzero.core.message.MessageAccessor;
import org.hzero.core.util.ResponseUtils;

import java.util.HashMap;
import java.util.List;

/**
 * <p>自定义SQL：固定值</p>
 *
 * @author feng.liu01@hand-china.com 2020-03-24 16:19:53
 */
@CheckItem("CUSTOM_FIXED_VALUE")
public class CustomFixedMeasure implements Measure {

    private final DatasourceFeign datasourceFeign;

    public CustomFixedMeasure(DatasourceFeign datasourceFeign) {
        this.datasourceFeign = datasourceFeign;
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

        BatchResultRuleDTO batchResultRuleDTO = new BatchResultRuleDTO();
//        batchResultRuleDTO.setExpectedValue(batchPlanTableLineDTO.getExpectedValue());
//        batchResultRuleDTO.setActualValue((String) response.get(0).values().toArray()[0]);
//
//        double actualValue = Double.parseDouble(batchResultRuleDTO.getActualValue());
//        double expectedValue = Double.parseDouble(batchPlanTableLineDTO.getExpectedValue());
//        MeasureUtil.fixedCompare(batchPlanTableLineDTO.getCompareWay(), actualValue, expectedValue, warningLevelList, batchResultRuleDTO);

        return batchResultRuleDTO;
    }
}
