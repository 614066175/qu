package com.hand.hdsp.quality.infra.measure.measure;

import com.fasterxml.jackson.core.type.TypeReference;
import com.hand.hdsp.quality.api.dto.DatasourceDTO;
import com.hand.hdsp.quality.domain.entity.BatchResultBase;
import com.hand.hdsp.quality.domain.entity.BatchResultItem;
import com.hand.hdsp.quality.domain.entity.ItemTemplateSql;
import com.hand.hdsp.quality.domain.repository.ItemTemplateSqlRepository;
import com.hand.hdsp.quality.infra.constant.ErrorCode;
import com.hand.hdsp.quality.infra.dataobject.MeasureParamDO;
import com.hand.hdsp.quality.infra.feign.DatasourceFeign;
import com.hand.hdsp.quality.infra.measure.*;
import io.choerodon.core.exception.CommonException;
import org.hzero.core.util.ResponseUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>通用SQL处理</p>
 *
 * @author feng.liu01@hand-china.com 2020-06-09 10:06:43
 */
@CheckItem("COMMON_SQL")
public class CommonSqlMeasure implements Measure {

    private final DatasourceFeign datasourceFeign;
    private final ItemTemplateSqlRepository templateSqlRepository;
    private final CountCollector countCollector;

    public CommonSqlMeasure(DatasourceFeign datasourceFeign,
                            ItemTemplateSqlRepository templateSqlRepository,
                            CountCollector countCollector) {
        this.datasourceFeign = datasourceFeign;
        this.templateSqlRepository = templateSqlRepository;
        this.countCollector = countCollector;
    }

    @Override
    public BatchResultItem check(MeasureParamDO param) {
        Long tenantId = param.getTenantId();
        DatasourceDTO datasourceDTO = param.getDatasourceDTO();
        BatchResultBase batchResultBase = param.getBatchResultBase();
        BatchResultItem batchResultItem = param.getBatchResultItem();
        List<ItemTemplateSql> list = templateSqlRepository.select(ItemTemplateSql.builder()
                .checkItem(param.getCheckItem())
                .datasourceType(param.getDatasourceType())
                .build());

        Map<String, String> variables = new HashMap<>(8);
        variables.put("table", batchResultBase.getObjectName());
        variables.put("field", MeasureUtil.handleFieldName(param.getFieldName()));
        variables.put("checkField", MeasureUtil.handleFieldName(param.getCheckFieldName()));

        datasourceDTO.setSql(MeasureUtil.replaceVariable(list.get(0).getSqlContent(), variables, batchResultBase.getWhereCondition()));

        List<HashMap<String, String>> response = ResponseUtils.getResponse(datasourceFeign.execSql(tenantId, datasourceDTO), new TypeReference<List<HashMap<String, String>>>() {
        });
        if (response.size() != 1 || response.get(0).size() != 1) {
            throw new CommonException(ErrorCode.CUSTOM_SQL_ONE_VALUE);
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
