package com.hand.hdsp.quality.infra.measure.measure;

import com.fasterxml.jackson.core.type.TypeReference;
import com.hand.hdsp.quality.api.dto.BatchPlanFieldLineDTO;
import com.hand.hdsp.quality.api.dto.BatchResultRuleDTO;
import com.hand.hdsp.quality.api.dto.DatasourceDTO;
import com.hand.hdsp.quality.domain.entity.BatchPlanField;
import com.hand.hdsp.quality.domain.entity.BatchResultItem;
import com.hand.hdsp.quality.infra.constant.PlanConstant;
import com.hand.hdsp.quality.infra.dataobject.MeasureParamDO;
import com.hand.hdsp.quality.infra.feign.DatasourceFeign;
import com.hand.hdsp.quality.infra.measure.CheckItem;
import com.hand.hdsp.quality.infra.measure.Measure;
import org.hzero.core.util.ResponseUtils;

import java.util.List;

/**
 * <p>正则表达式</p>
 *
 * @author feng.liu01@hand-china.com 2020-03-24 16:19:53
 */
@CheckItem(PlanConstant.CheckWay.REGULAR)
public class RegularMeasure implements Measure {

    private static final String SQL = "SELECT COUNT(*) actualValue FROM %s WHERE %s REGEXP '%s'";
    private static final String SQL_ALL = "SELECT COUNT(*) actualValue FROM %s";
    private final DatasourceFeign datasourceFeign;

    public RegularMeasure(DatasourceFeign datasourceFeign) {
        this.datasourceFeign = datasourceFeign;
    }

    @Override
    public BatchResultItem check(MeasureParamDO param) {
        Long tenantId = param.getTenantId();
        DatasourceDTO datasourceDTO = param.getDatasourceDTO();
        BatchPlanField batchPlanField = param.getBatchPlanField();
        BatchPlanFieldLineDTO batchPlanFieldLineDTO = param.getBatchPlanFieldLineDTO();


//        datasourceDTO.setSql(String.format(SQL, datasourceDTO.getTableName(), batchPlanFieldLineDTO.getFieldName(), batchPlanFieldLineDTO.getRegularExpression()));
        List<BatchResultRuleDTO> regResultList = ResponseUtils.getResponse(datasourceFeign.execSql(tenantId, datasourceDTO), new TypeReference<List<BatchResultRuleDTO>>() {
        });

        datasourceDTO.setSql(String.format(SQL_ALL, datasourceDTO.getTableName()));
        List<BatchResultRuleDTO> allList = ResponseUtils.getResponse(datasourceFeign.execSql(tenantId, datasourceDTO), new TypeReference<List<BatchResultRuleDTO>>() {
        });

        BatchResultRuleDTO batchResultRuleDTO = new BatchResultRuleDTO();

//        double actualValue = Double.parseDouble(regResultList.get(0).getActualValue());
//        double expectedValue = Double.parseDouble(allList.get(0).getActualValue());

//        if (actualValue != expectedValue) {
//            batchResultRuleDTO.setWarningLevel(warningLevelList.get(0).getWarningLevel());
//            batchResultRuleDTO.setExceptionInfo("不满足正则表达式");
//        }

        return param.getBatchResultItem();
    }
}
