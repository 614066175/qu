package com.hand.hdsp.quality.infra.measure.table;

import com.fasterxml.jackson.core.type.TypeReference;
import com.hand.hdsp.quality.api.dto.BatchPlanTableLineDTO;
import com.hand.hdsp.quality.api.dto.BatchResultRuleDTO;
import com.hand.hdsp.quality.api.dto.DatasourceDTO;
import com.hand.hdsp.quality.domain.entity.PlanWarningLevel;
import com.hand.hdsp.quality.infra.dataobject.MeasureParamDO;
import com.hand.hdsp.quality.infra.feign.DatasourceFeign;
import com.hand.hdsp.quality.infra.measure.CheckItem;
import com.hand.hdsp.quality.infra.measure.Measure;
import com.hand.hdsp.quality.infra.measure.MeasureUtil;
import io.choerodon.core.exception.CommonException;
import org.hzero.core.util.ResponseUtils;

import java.util.List;

/**
 * <p>表行数:固定值</p>
 *
 * @author feng.liu01@hand-china.com 2020-03-24 16:19:53
 */
@CheckItem("TABLE_LINE_FIXED_VALUE")
public class LineFixedValueMeasure implements Measure {

    private static final String SQL = "select count(*) actualValue from %s";
    private final DatasourceFeign datasourceFeign;

    public LineFixedValueMeasure(DatasourceFeign datasourceFeign) {
        this.datasourceFeign = datasourceFeign;
    }

    @Override
    public BatchResultRuleDTO check(MeasureParamDO param) {
        Long tenantId = param.getTenantId();
        DatasourceDTO datasourceDTO = param.getDatasourceDTO();
        BatchPlanTableLineDTO batchPlanTableLineDTO = param.getBatchPlanTableLineDTO();
        List<PlanWarningLevel> warningLevelList = param.getWarningLevelList();

        datasourceDTO.setSql(String.format(SQL, datasourceDTO.getTableName()));
        List<BatchResultRuleDTO> batchResultRuleDTOList = ResponseUtils.getResponse(datasourceFeign.execSql(tenantId, datasourceDTO), new TypeReference<List<BatchResultRuleDTO>>() {
        }, (httpStatus, response) -> {
            throw new CommonException(response);
        }, exceptionResponse -> {
            throw new CommonException(exceptionResponse.getMessage());
        });
        BatchResultRuleDTO batchResultRuleDTO = batchResultRuleDTOList.get(0);
        batchResultRuleDTO.setExpectedValue(batchPlanTableLineDTO.getExpectedValue());

        long actualValue = Long.parseLong(batchResultRuleDTO.getActualValue());
        long expectedValue = Long.parseLong(batchPlanTableLineDTO.getExpectedValue());
        boolean result = MeasureUtil.compary(batchPlanTableLineDTO.getCompareWay(), actualValue, expectedValue);
        if (result) {
            batchResultRuleDTO.setWarningLevel(warningLevelList.get(0).getWarningLevel());
        }
        return batchResultRuleDTO;
    }
}
