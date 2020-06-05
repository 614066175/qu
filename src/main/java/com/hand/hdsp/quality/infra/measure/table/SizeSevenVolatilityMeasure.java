package com.hand.hdsp.quality.infra.measure.table;

import com.fasterxml.jackson.core.type.TypeReference;
import com.hand.hdsp.quality.api.dto.BatchPlanTableLineDTO;
import com.hand.hdsp.quality.api.dto.BatchResultRuleDTO;
import com.hand.hdsp.quality.api.dto.DatasourceDTO;
import com.hand.hdsp.quality.domain.entity.BatchResultBase;
import com.hand.hdsp.quality.infra.dataobject.BatchResultBaseDO;
import com.hand.hdsp.quality.infra.dataobject.MeasureParamDO;
import com.hand.hdsp.quality.infra.feign.DatasourceFeign;
import com.hand.hdsp.quality.infra.mapper.BatchResultBaseMapper;
import com.hand.hdsp.quality.infra.measure.CheckItem;
import com.hand.hdsp.quality.infra.measure.Measure;
import org.apache.commons.lang3.time.DateUtils;
import org.hzero.core.util.ResponseUtils;
import org.springframework.util.Assert;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * <p>表大小:7天波动率</p>
 *
 * @author feng.liu01@hand-china.com 2020-03-24 16:19:53
 */
@CheckItem("TABLE_SIZE_SEVEN_VOLATILITY")
public class SizeSevenVolatilityMeasure implements Measure {

    private final BatchResultBaseMapper batchResultBaseMapper;
    private final DatasourceFeign datasourceFeign;

    public SizeSevenVolatilityMeasure(BatchResultBaseMapper batchResultBaseMapper, DatasourceFeign datasourceFeign) {
        this.batchResultBaseMapper = batchResultBaseMapper;
        this.datasourceFeign = datasourceFeign;
    }

    @Override
    public BatchResultRuleDTO check(MeasureParamDO param) {
        BatchPlanTableLineDTO batchPlanTableLineDTO = param.getBatchPlanTableLineDTO();

        DatasourceDTO datasourceDTO = param.getDatasourceDTO();

        BatchResultBase batchResultBase = param.getBatchResultBase();
        Long sample = batchResultBase.getTableSize();
        if (sample == null) {
            datasourceDTO.setSql(String.format(SizeFixedValueMeasure.TABLE_LENGTH_SQL, datasourceDTO.getSchema(), datasourceDTO.getTableName()));
            List<Map<String, Long>> result = ResponseUtils.getResponse(datasourceFeign.execSql(datasourceDTO.getTenantId(), datasourceDTO), new TypeReference<List<Map<String, Long>>>() {
            });
            Assert.notEmpty(result, "查询表大小失败，请联系系统管理员！");
            sample = result.get(0).get("data_length");
            batchResultBase.setTableSize(sample);
        }

        BatchResultRuleDTO batchResultRuleDTO = new BatchResultRuleDTO();
//        batchResultRuleDTO.setExpectedValue(batchPlanTableLineDTO.getExpectedValue());
        // 查询一天前的表行数
        List<BatchResultBaseDO> resultList = batchResultBaseMapper.queryList(BatchResultBaseDO.builder()
                .planBaseId(param.getBatchResultBase().getPlanBaseId())
                .measureDate(DateUtils.addDays(new Date(), -7))
                .build());
        if (resultList.isEmpty()) {
            return batchResultRuleDTO;
        }

        long base = resultList.get(0).getTableSize();
//        MeasureUtil.volatilityCompare(batchPlanTableLineDTO.getCompareWay(), BigDecimal.valueOf(sample), BigDecimal.valueOf(base),
//                warningLevelList, batchResultRuleDTO);
        return batchResultRuleDTO;
    }
}
