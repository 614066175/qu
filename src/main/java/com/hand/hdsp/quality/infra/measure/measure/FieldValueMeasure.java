package com.hand.hdsp.quality.infra.measure.measure;

import com.fasterxml.jackson.core.type.TypeReference;
import com.hand.hdsp.quality.api.dto.DatasourceDTO;
import com.hand.hdsp.quality.api.dto.WarningLevelDTO;
import com.hand.hdsp.quality.domain.entity.BatchResultBase;
import com.hand.hdsp.quality.domain.entity.BatchResultItem;
import com.hand.hdsp.quality.domain.entity.ItemTemplateSql;
import com.hand.hdsp.quality.domain.repository.ItemTemplateSqlRepository;
import com.hand.hdsp.quality.infra.constant.PlanConstant;
import com.hand.hdsp.quality.infra.dataobject.MeasureParamDO;
import com.hand.hdsp.quality.infra.feign.DatasourceFeign;
import com.hand.hdsp.quality.infra.measure.CheckItem;
import com.hand.hdsp.quality.infra.measure.Measure;
import com.hand.hdsp.quality.infra.measure.MeasureCollector;
import com.hand.hdsp.quality.infra.measure.MeasureUtil;
import io.choerodon.core.exception.CommonException;
import org.apache.commons.collections4.CollectionUtils;
import org.hzero.boot.platform.lov.adapter.LovAdapter;
import org.hzero.boot.platform.lov.dto.LovValueDTO;
import org.hzero.core.base.BaseConstants;
import org.hzero.core.util.ResponseUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>
 * 字段值
 * 特殊处理值集校验
 * 其他校验类型继续走通用SQL
 * </p>
 *
 * @author feng.liu01@hand-china.com 2020-06-09 10:06:43
 */
@CheckItem("FIELD_VALUE")
public class FieldValueMeasure implements Measure {

    private final DatasourceFeign datasourceFeign;
    private final ItemTemplateSqlRepository templateSqlRepository;
    private final MeasureCollector measureCollector;
    private final LovAdapter lovAdapter;

    public FieldValueMeasure(DatasourceFeign datasourceFeign,
                             ItemTemplateSqlRepository templateSqlRepository,
                             MeasureCollector measureCollector, LovAdapter lovAdapter) {
        this.datasourceFeign = datasourceFeign;
        this.templateSqlRepository = templateSqlRepository;
        this.measureCollector = measureCollector;
        this.lovAdapter = lovAdapter;
    }

    @Override
    public BatchResultItem check(MeasureParamDO param) {
        // 非值集校验，继续走通用SQL逻辑
        String countType = param.getCountType();
        if (!PlanConstant.CountType.LOV_VALUE.equals(countType)) {
            Measure measure = measureCollector.getMeasure(PlanConstant.COMMON_SQL);
            return measure.check(param);
        }

        Long tenantId = param.getTenantId();
        DatasourceDTO datasourceDTO = param.getDatasourceDTO();
        BatchResultBase batchResultBase = param.getBatchResultBase();
        BatchResultItem batchResultItem = param.getBatchResultItem();
        WarningLevelDTO warningLevelDTO = param.getWarningLevelList().get(0);
        List<LovValueDTO> lovValueDTOList = lovAdapter.queryLovValue(warningLevelDTO.getLovCode(), tenantId);
        if (CollectionUtils.isEmpty(lovValueDTOList)) {
            throw new CommonException("未查询到值集的值");
        }

        // 查询要执行的SQL
        ItemTemplateSql itemTemplateSql = templateSqlRepository.selectSql(ItemTemplateSql.builder()
                .checkItem(countType + "_" + warningLevelDTO.getCompareSymbol())
                .datasourceType(batchResultBase.getDatasourceType())
                .build());

        Map<String, String> variables = new HashMap<>(8);
        variables.put("table", batchResultBase.getObjectName());
        variables.put("field", MeasureUtil.handleFieldName(param.getFieldName()));
        variables.put("listValue", lovValueDTOList.stream()
                .map(lovValueDTO -> "'" + lovValueDTO.getValue() + "'")
                .collect(Collectors.joining(BaseConstants.Symbol.COMMA))
        );

        datasourceDTO.setSql(MeasureUtil.replaceVariable(itemTemplateSql.getSqlContent(), variables, param.getWhereCondition()));

        List<HashMap<String, Long>> response = ResponseUtils.getResponse(datasourceFeign.execSql(tenantId, datasourceDTO), new TypeReference<List<HashMap<String, Long>>>() {
        });

        if (CollectionUtils.isNotEmpty(response)) {
            batchResultItem.setWarningLevel(warningLevelDTO.getWarningLevel());
        }
        return batchResultItem;
    }
}
