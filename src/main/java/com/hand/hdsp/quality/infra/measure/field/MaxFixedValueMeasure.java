package com.hand.hdsp.quality.infra.measure.field;

import com.fasterxml.jackson.core.type.TypeReference;
import com.hand.hdsp.quality.api.dto.BatchPlanFieldDTO;
import com.hand.hdsp.quality.api.dto.BatchPlanFieldLineDTO;
import com.hand.hdsp.quality.api.dto.BatchResultRuleDTO;
import com.hand.hdsp.quality.api.dto.DatasourceDTO;
import com.hand.hdsp.quality.domain.entity.PlanWarningLevel;
import com.hand.hdsp.quality.infra.dataobject.MeasureParamDO;
import com.hand.hdsp.quality.infra.feign.DatasourceFeign;
import com.hand.hdsp.quality.infra.measure.Measure;
import com.hand.hdsp.quality.infra.measure.MeasureUtil;
import io.choerodon.core.exception.CommonException;
import org.hzero.core.util.ResponseUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * <p>表行数:固定值</p>
 *
 * @author feng.liu01@hand-china.com 2020-03-24 16:19:53
 */
public class MaxFixedValueMeasure implements Measure {

    private static final String SQL = "select max(%s) actualValue from %s";
    @Autowired
    private DatasourceFeign datasourceFeign;

    @Override
    public BatchResultRuleDTO check(MeasureParamDO param) {
        Long tenantId = param.getTenantId();
        DatasourceDTO datasourceDTO = param.getDatasourceDTO();
        BatchPlanFieldDTO batchPlanFieldDTO = param.getBatchPlanFieldDTO();
        BatchPlanFieldLineDTO batchPlanFieldLineDTO = param.getBatchPlanFieldLineDTO();
        List<PlanWarningLevel> warningLevelList = param.getWarningLevelList();

        datasourceDTO.setSql(String.format(SQL, batchPlanFieldDTO.getFieldName(), datasourceDTO.getTableName()));
        List<BatchResultRuleDTO> batchResultRuleDTOList = ResponseUtils.getResponse(datasourceFeign.execSql(tenantId, datasourceDTO), new TypeReference<List<BatchResultRuleDTO>>() {
        }, (httpStatus, response) -> {
            throw new CommonException(response);
        }, exceptionResponse -> {
            throw new CommonException(exceptionResponse.getMessage());
        });
        BatchResultRuleDTO batchResultRuleDTO = batchResultRuleDTOList.get(0);
        batchResultRuleDTO.setExpectedValue(batchPlanFieldLineDTO.getExpectedValue());

        long actualValue = Long.parseLong(batchResultRuleDTO.getActualValue());
        long expectedValue = Long.parseLong(batchPlanFieldLineDTO.getExpectedValue());
        MeasureUtil.fixedCompare(batchPlanFieldLineDTO.getCompareWay(), actualValue, expectedValue, warningLevelList, batchResultRuleDTO);

        return batchResultRuleDTO;
    }
}
