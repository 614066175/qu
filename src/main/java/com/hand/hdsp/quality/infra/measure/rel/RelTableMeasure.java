package com.hand.hdsp.quality.infra.measure.rel;

import com.fasterxml.jackson.core.type.TypeReference;
import com.hand.hdsp.quality.api.dto.BatchResultRuleDTO;
import com.hand.hdsp.quality.api.dto.DatasourceDTO;
import com.hand.hdsp.quality.domain.entity.BatchPlanRelTable;
import com.hand.hdsp.quality.domain.entity.BatchPlanRelTableLine;
import com.hand.hdsp.quality.domain.entity.BatchResultBase;
import com.hand.hdsp.quality.domain.entity.PlanWarningLevel;
import com.hand.hdsp.quality.infra.constant.PlanConstant;
import com.hand.hdsp.quality.infra.dataobject.MeasureParamDO;
import com.hand.hdsp.quality.infra.feign.DatasourceFeign;
import com.hand.hdsp.quality.infra.measure.CheckItem;
import com.hand.hdsp.quality.infra.measure.Measure;
import io.choerodon.core.exception.CommonException;
import org.hzero.core.base.BaseConstants;
import org.hzero.core.util.ResponseUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;

/**
 * <p>表间关系</p>
 *
 * @author feng.liu01@hand-china.com 2020-03-24 16:19:53
 */
@CheckItem(PlanConstant.RuleType.TABLE_RELATION)
public class RelTableMeasure implements Measure {

    @Autowired
    private DatasourceFeign datasourceFeign;

    @Override
    public BatchResultRuleDTO check(MeasureParamDO param) {
        BatchResultBase batchResultBase = param.getBatchResultBase();
        BatchPlanRelTable batchPlanRelTable = param.getBatchPlanRelTable();
        List<BatchPlanRelTableLine> lineList = param.getBatchPlanRelTableLineList();
        DatasourceDTO datasourceDTO = param.getDatasourceDTO();
        List<PlanWarningLevel> warningLevelList = param.getWarningLevelList();

        StringBuilder sql = new StringBuilder();
        sql.append("select count(*) count from ")
                .append(batchResultBase.getTableName())
                .append(" source, ")
                .append("( select * from ")
                .append(batchPlanRelTable.getRelTableName())
                .append(" where ")
                .append(batchPlanRelTable.getWhereCondition())
                .append(" ) rel ");

        if (!lineList.isEmpty()) {
            sql.append(" where 1 = 1 ");
            for (BatchPlanRelTableLine batchPlanRelTableLine : lineList) {
                sql.append(" and source.").append(batchPlanRelTableLine.getSourceFieldName())
                        .append(batchPlanRelTableLine.getRelCode())
                        .append(" rel.").append(batchPlanRelTableLine.getRelFieldName());
            }
        }

        datasourceDTO.setSql(sql.toString());
        List<Map<String, Long>> result = ResponseUtils.getResponse(datasourceFeign.execSql(datasourceDTO.getTenantId(), datasourceDTO), new TypeReference<List<Map<String, Long>>>() {
        }, (httpStatus, response) -> {
            throw new CommonException(response);
        }, exceptionResponse -> {
            throw new CommonException(exceptionResponse.getMessage());
        });

        //计算准确率
        BigDecimal a = new BigDecimal(result.get(0).get("count"));
        BigDecimal b = BigDecimal.valueOf(batchResultBase.getDataCount());
        BigDecimal rate = a.divide(b, 2, RoundingMode.HALF_UP).multiply(new BigDecimal(100));

        BatchResultRuleDTO batchResultRuleDTO = new BatchResultRuleDTO();
        batchResultRuleDTO.setActualValue(rate.toString() + BaseConstants.Symbol.PERCENTAGE);

        for (PlanWarningLevel planWarningLevel : warningLevelList) {
            if (planWarningLevel.getStartValue().compareTo(rate) <= 0
                    && planWarningLevel.getEndValue().compareTo(rate) >= 0) {
                batchResultRuleDTO.setWarningLevel(planWarningLevel.getWarningLevel());
            }
        }

        return batchResultRuleDTO;
    }
}
