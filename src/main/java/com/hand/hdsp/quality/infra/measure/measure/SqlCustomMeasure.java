package com.hand.hdsp.quality.infra.measure.measure;

import com.fasterxml.jackson.core.type.TypeReference;
import com.hand.hdsp.quality.api.dto.DatasourceDTO;
import com.hand.hdsp.quality.domain.entity.BatchResultItem;
import com.hand.hdsp.quality.infra.constant.ErrorCode;
import com.hand.hdsp.quality.infra.constant.PlanConstant;
import com.hand.hdsp.quality.infra.dataobject.MeasureParamDO;
import com.hand.hdsp.quality.infra.feign.DatasourceFeign;
import com.hand.hdsp.quality.infra.measure.CheckItem;
import com.hand.hdsp.quality.infra.measure.Count;
import com.hand.hdsp.quality.infra.measure.CountCollector;
import com.hand.hdsp.quality.infra.measure.Measure;
import io.choerodon.core.exception.CommonException;
import org.apache.commons.lang3.StringUtils;
import org.hzero.core.util.ResponseUtils;

import java.util.HashMap;
import java.util.List;

/**
 * <p>自定义SQL</p>
 *
 * @author feng.liu01@hand-china.com 2020-03-24 16:19:53
 */
@CheckItem(PlanConstant.RuleType.SQL_CUSTOM)
public class SqlCustomMeasure implements Measure {

    private final DatasourceFeign datasourceFeign;
    private final CountCollector countCollector;

    public SqlCustomMeasure(DatasourceFeign datasourceFeign, CountCollector countCollector) {
        this.datasourceFeign = datasourceFeign;
        this.countCollector = countCollector;
    }

    @Override
    public BatchResultItem check(MeasureParamDO param) {
        Long tenantId = param.getTenantId();
        BatchResultItem batchResultItem = param.getBatchResultItem();
        DatasourceDTO datasourceDTO = param.getDatasourceDTO();
        String sql = datasourceDTO.getSql();
        if (StringUtils.isNotBlank(param.getWhereCondition())) {
            sql = sql + " where " + param.getWhereCondition();
            datasourceDTO.setSql(sql);
        }

        List<HashMap<String, String>> response = ResponseUtils.getResponse(
                datasourceFeign.execSql(tenantId, datasourceDTO),
                new TypeReference<List<HashMap<String, String>>>() {
                },
                ResponseUtils.DEFAULT_NOT_2XX_SUCCESSFUL,
                exceptionResponse -> {
                    throw new CommonException(exceptionResponse.getMessage());
                });
        if (response.size() != 1 || response.get(0).size() != 1) {
            throw new CommonException(ErrorCode.CHECK_ITEM_ONE_VALUE);
        }

        String value = (String) response.get(0).values().toArray()[0];
        param.setCountValue(value);
        batchResultItem.setActualValue(value);
        batchResultItem.setCurrentValue(value);
        Count count = countCollector.getCount(param.getCountType());
        count.count(param);

        return batchResultItem;
    }
}
