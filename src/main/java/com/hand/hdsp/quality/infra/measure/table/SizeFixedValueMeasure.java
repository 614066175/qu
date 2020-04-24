package com.hand.hdsp.quality.infra.measure.table;

import com.fasterxml.jackson.core.type.TypeReference;
import com.hand.hdsp.quality.api.dto.BatchPlanTableLineDTO;
import com.hand.hdsp.quality.api.dto.BatchResultRuleDTO;
import com.hand.hdsp.quality.api.dto.DatasourceDTO;
import com.hand.hdsp.quality.domain.entity.BatchResultBase;
import com.hand.hdsp.quality.domain.entity.PlanWarningLevel;
import com.hand.hdsp.quality.infra.dataobject.MeasureParamDO;
import com.hand.hdsp.quality.infra.feign.DatasourceFeign;
import com.hand.hdsp.quality.infra.measure.CheckItem;
import com.hand.hdsp.quality.infra.measure.Measure;
import com.hand.hdsp.quality.infra.measure.MeasureUtil;
import org.hzero.core.util.ResponseUtils;
import org.springframework.util.Assert;

import java.util.List;
import java.util.Map;

/**
 * <p>表大小:固定值</p>
 *
 * @author feng.liu01@hand-china.com 2020-03-24 16:19:53
 */
@CheckItem("TABLE_SIZE_FIXED_VALUE")
public class SizeFixedValueMeasure implements Measure {

    public static final String TABLE_LENGTH_SQL = "SELECT data_length FROM information_schema.TABLES WHERE table_schema='%s' AND table_name='%s'";

    private final DatasourceFeign datasourceFeign;

    public SizeFixedValueMeasure(DatasourceFeign datasourceFeign) {
        this.datasourceFeign = datasourceFeign;
    }


    @Override
    public BatchResultRuleDTO check(MeasureParamDO param) {
        BatchPlanTableLineDTO batchPlanTableLineDTO = param.getBatchPlanTableLineDTO();
        List<PlanWarningLevel> warningLevelList = param.getWarningLevelList();
        DatasourceDTO datasourceDTO = param.getDatasourceDTO();
        BatchResultBase batchResultBase = param.getBatchResultBase();
        Long actualValue = batchResultBase.getTableSize();
        if (actualValue == null) {
            datasourceDTO.setSql(String.format(TABLE_LENGTH_SQL, datasourceDTO.getSchema(), datasourceDTO.getTableName()));
            List<Map<String, Long>> result = ResponseUtils.getResponse(datasourceFeign.execSql(datasourceDTO.getTenantId(), datasourceDTO), new TypeReference<List<Map<String, Long>>>() {
            });
            Assert.notEmpty(result, "查询表大小失败，请联系系统管理员！");
            actualValue = result.get(0).get("data_length");
            batchResultBase.setTableSize(actualValue);
        }

        BatchResultRuleDTO batchResultRuleDTO = new BatchResultRuleDTO();
        batchResultRuleDTO.setExpectedValue(batchPlanTableLineDTO.getExpectedValue());

        long expectedValue = Long.parseLong(batchPlanTableLineDTO.getExpectedValue());
        MeasureUtil.fixedCompare(batchPlanTableLineDTO.getCompareWay(), actualValue, expectedValue, warningLevelList, batchResultRuleDTO);

        batchResultRuleDTO.setActualValue(actualValue + "");
        return batchResultRuleDTO;
    }
}
