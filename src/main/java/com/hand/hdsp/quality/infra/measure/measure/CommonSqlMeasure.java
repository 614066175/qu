package com.hand.hdsp.quality.infra.measure.measure;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.hand.hdsp.quality.domain.entity.BatchResultBase;
import com.hand.hdsp.quality.domain.entity.BatchResultItem;
import com.hand.hdsp.quality.domain.entity.ItemTemplateSql;
import com.hand.hdsp.quality.domain.repository.ItemTemplateSqlRepository;
import com.hand.hdsp.quality.infra.constant.ErrorCode;
import com.hand.hdsp.quality.infra.constant.PlanConstant;
import com.hand.hdsp.quality.infra.dataobject.MeasureParamDO;
import com.hand.hdsp.quality.infra.measure.*;
import io.choerodon.core.exception.CommonException;
import org.hzero.boot.driver.app.service.DriverSessionService;
import org.hzero.starter.driver.core.session.DriverSession;

/**
 * <p>通用SQL处理</p>
 *
 * @author feng.liu01@hand-china.com 2020-06-09 10:06:43
 */
@CheckItem(PlanConstant.COMMON_SQL)
public class CommonSqlMeasure implements Measure {

    private final ItemTemplateSqlRepository templateSqlRepository;
    private final CountCollector countCollector;
    private final DriverSessionService driverSessionService;

    public CommonSqlMeasure(ItemTemplateSqlRepository templateSqlRepository,
                            CountCollector countCollector, DriverSessionService driverSessionService) {
        this.templateSqlRepository = templateSqlRepository;
        this.countCollector = countCollector;
        this.driverSessionService = driverSessionService;
    }

    @Override
    public BatchResultItem check(MeasureParamDO param) {
        Long tenantId = param.getTenantId();
        BatchResultBase batchResultBase = param.getBatchResultBase();
        BatchResultItem batchResultItem = param.getBatchResultItem();

        // 查询要执行的SQL
        ItemTemplateSql itemTemplateSql = templateSqlRepository.selectSql(ItemTemplateSql.builder()
                .checkItem(param.getCheckItem())
                .datasourceType(batchResultBase.getDatasourceType())
                .build());

        Map<String, String> variables = new HashMap<>(8);
        variables.put("table", batchResultBase.getPackageObjectName());
        variables.put("field", MeasureUtil.handleFieldName(param.getFieldName()));

        DriverSession driverSession = driverSessionService.getDriverSession(tenantId,param.getPluginDatasourceDTO().getDatasourceCode());
        List<Map<String, Object>> response = driverSession.executeOneQuery(param.getSchema(), MeasureUtil.replaceVariable(itemTemplateSql.getSqlContent(), variables, param.getWhereCondition()));
        if (response.size() != 1 || response.get(0).size() != 1) {
            throw new CommonException(ErrorCode.CHECK_ITEM_ONE_VALUE);
        }

        String value = response.get(0).values().toArray()[0].toString();
        param.setCountValue(value);
        batchResultItem.setActualValue(value);
        batchResultItem.setCurrentValue(value);
        Count count = countCollector.getCount(param.getCountType());
        count.count(param);

        return batchResultItem;
    }
}
