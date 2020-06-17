package com.hand.hdsp.quality.infra.measure.measure;

import com.fasterxml.jackson.core.type.TypeReference;
import com.hand.hdsp.quality.api.dto.BatchPlanTableLineDTO;
import com.hand.hdsp.quality.api.dto.DatasourceDTO;
import com.hand.hdsp.quality.domain.entity.BatchResultItem;
import com.hand.hdsp.quality.infra.constant.ErrorCode;
import com.hand.hdsp.quality.infra.dataobject.MeasureParamDO;
import com.hand.hdsp.quality.infra.feign.DatasourceFeign;
import com.hand.hdsp.quality.infra.measure.CheckItem;
import com.hand.hdsp.quality.infra.measure.Count;
import com.hand.hdsp.quality.infra.measure.CountCollector;
import com.hand.hdsp.quality.infra.measure.Measure;
import io.choerodon.core.exception.CommonException;
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
    private final CountCollector countCollector;

    public CustomFixedMeasure(DatasourceFeign datasourceFeign, CountCollector countCollector) {
        this.datasourceFeign = datasourceFeign;
        this.countCollector = countCollector;
    }

    @Override
    public BatchResultItem check(MeasureParamDO param) {
        Long tenantId = param.getTenantId();
        DatasourceDTO datasourceDTO = param.getDatasourceDTO();
        BatchPlanTableLineDTO batchPlanTableLineDTO = param.getBatchPlanTableLineDTO();


        datasourceDTO.setSql(batchPlanTableLineDTO.getCustomSql());

        List<HashMap<String, String>> response = ResponseUtils.getResponse(datasourceFeign.execSql(tenantId, datasourceDTO), new TypeReference<List<HashMap<String, String>>>() {
        });
        if (response.size() != 1 || response.get(0).size() != 1) {
            throw new CommonException(ErrorCode.CUSTOM_SQL_ONE_VALUE);
        }

        String value = (String) response.get(0).values().toArray()[0];
        param.setCountValue(value);
        Count count = countCollector.getCount(param.getCountType());
        count.count(param);

        return param.getBatchResultItem();
    }
}
