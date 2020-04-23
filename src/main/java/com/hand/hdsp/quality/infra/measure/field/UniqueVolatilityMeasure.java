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
 * <p>唯一值个数,1、7、30天波动率</p>
 *
 * @author feng.liu01@hand-china.com 2020-03-24 16:19:53
 */
@CheckItem("FIELD_UNIQUE_VOLATILITY")
public class UniqueVolatilityMeasure implements Measure {

    private static final String SQL = "SELECT COUNT(*) currentValue FROM (SELECT %s FROM %s GROUP BY %s HAVING COUNT(*) = 1) a";
    private final DatasourceFeign datasourceFeign;
    private final BatchResultRuleMapper batchResultRuleMapper;

    public UniqueVolatilityMeasure(DatasourceFeign datasourceFeign, BatchResultRuleMapper batchResultRuleMapper) {
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

        datasourceDTO.setSql(String.format(SQL, batchPlanField.getFieldName(), datasourceDTO.getTableName(), batchPlanField.getFieldName()));
        List<BatchResultRuleDTO> batchResultRuleDTOList = ResponseUtils.getResponse(datasourceFeign.execSql(tenantId, datasourceDTO), new TypeReference<List<BatchResultRuleDTO>>() {
        });
        String currentValue = batchResultRuleDTOList.get(0).getCurrentValue();

        BatchResultRuleDTO batchResultRuleDTO = new BatchResultRuleDTO();
        batchResultRuleDTO.setCurrentValue(currentValue);

        //查询基础值(1天)
        List<BatchResultRuleDO> oneList = batchResultRuleMapper.queryList(BatchResultRuleDO.builder()
                .planFieldLineId(batchPlanFieldLineDTO.getPlanFieldLineId())
                .measureDate(DateUtils.addDays(new Date(), -1))
                .build());
        BatchResultRuleDTO oneResult = new BatchResultRuleDTO();
        if (oneList.isEmpty()) {
            oneResult.setWaveRate("0%");
        } else {
            MeasureUtil.volatilityCompare(batchPlanFieldLineDTO.getCompareWay(),
                    new BigDecimal(currentValue),
                    new BigDecimal(oneList.get(0).getCurrentValue()),
                    warningLevelList, oneResult);
        }


        //查询基础值(7天)
        List<BatchResultRuleDO> sevenList = batchResultRuleMapper.queryList(BatchResultRuleDO.builder()
                .planFieldLineId(batchPlanFieldLineDTO.getPlanFieldLineId())
                .measureDate(DateUtils.addDays(new Date(), -7))
                .build());
        BatchResultRuleDTO sevenResult = new BatchResultRuleDTO();
        if (sevenList.isEmpty()) {
            sevenResult.setWaveRate("0%");
        } else {
            MeasureUtil.volatilityCompare(batchPlanFieldLineDTO.getCompareWay(),
                    new BigDecimal(currentValue),
                    new BigDecimal(sevenList.get(0).getCurrentValue()),
                    warningLevelList, sevenResult);
        }
        //查询基础值(30天)
        List<BatchResultRuleDO> thirtyList = batchResultRuleMapper.queryList(BatchResultRuleDO.builder()
                .planFieldLineId(batchPlanFieldLineDTO.getPlanFieldLineId())
                .measureDate(DateUtils.addDays(new Date(), -30))
                .build());
        BatchResultRuleDTO thirtyResult = new BatchResultRuleDTO();
        if (thirtyList.isEmpty()) {
            thirtyResult.setWaveRate("0%");
        } else {
            MeasureUtil.volatilityCompare(batchPlanFieldLineDTO.getCompareWay(),
                    new BigDecimal(currentValue),
                    new BigDecimal(thirtyList.get(0).getCurrentValue()),
                    warningLevelList, thirtyResult);
        }

        //最终结果
        batchResultRuleDTO.setWaveRate(oneResult.getWaveRate() + "、" + sevenResult.getWaveRate() + "、" + thirtyResult.getWaveRate());
        if (oneResult.getWarningLevel() != null) {
            batchResultRuleDTO.setWarningLevel(oneResult.getWarningLevel());
            batchResultRuleDTO.setExceptionInfo(oneResult.getExceptionInfo());
        } else if (sevenResult.getWarningLevel() != null) {
            batchResultRuleDTO.setWarningLevel(sevenResult.getWarningLevel());
            batchResultRuleDTO.setExceptionInfo(sevenResult.getExceptionInfo());
        } else {
            batchResultRuleDTO.setWarningLevel(thirtyResult.getWarningLevel());
            batchResultRuleDTO.setExceptionInfo(thirtyResult.getExceptionInfo());
        }
        return batchResultRuleDTO;
    }
}
