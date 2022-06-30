package com.hand.hdsp.quality.infra.measure.measure;

import com.alibaba.druid.DbType;
import com.hand.hdsp.quality.domain.entity.BatchResultItem;
import com.hand.hdsp.quality.infra.constant.ErrorCode;
import com.hand.hdsp.quality.infra.constant.PlanConstant;
import com.hand.hdsp.quality.infra.dataobject.MeasureParamDO;
import com.hand.hdsp.quality.infra.measure.CheckItem;
import com.hand.hdsp.quality.infra.measure.Count;
import com.hand.hdsp.quality.infra.measure.CountCollector;
import com.hand.hdsp.quality.infra.measure.Measure;
import io.choerodon.core.exception.CommonException;
import org.apache.commons.lang3.StringUtils;
import org.hzero.boot.driver.app.service.DriverSessionService;
import org.hzero.starter.driver.core.session.DriverSession;

import java.util.List;
import java.util.Map;

/**
 * <p>自定义SQL</p>
 *
 * @author feng.liu01@hand-china.com 2020-03-24 16:19:53
 */
@CheckItem(PlanConstant.RuleType.SQL_CUSTOM)
public class SqlCustomMeasure implements Measure {

    private final CountCollector countCollector;
    private final DriverSessionService driverSessionService;

    public SqlCustomMeasure(CountCollector countCollector, DriverSessionService driverSessionService) {
        this.countCollector = countCollector;
        this.driverSessionService = driverSessionService;
    }

    @Override
    public BatchResultItem check(MeasureParamDO param) {
        Long tenantId = param.getTenantId();
        BatchResultItem batchResultItem = param.getBatchResultItem();
        String sql = param.getSql();
        if (StringUtils.isNotBlank(param.getWhereCondition())) {
            sql = sql + " where " + param.getWhereCondition();
            param.setSql(sql);
        }
        DriverSession driverSession = driverSessionService.getDriverSession(tenantId, param.getPluginDatasourceDTO().getDatasourceCode());
        List<Map<String, Object>> response = driverSession.executeOneQuery(param.getSchema(), param.getSql());
        if (!DbType.hive.equals(driverSession.getDbType())) {
            if (response.size() != 1 || response.get(0).size() != 1) {
                throw new CommonException(ErrorCode.CHECK_ITEM_ONE_VALUE);
            }
        }

        String value = String.valueOf(response.get(0).values().toArray()[0]);
        param.setCountValue(value);
        batchResultItem.setActualValue(value);
        batchResultItem.setCurrentValue(value);
        Count count = countCollector.getCount(param.getCountType());
        count.count(param);

        return batchResultItem;
    }
}
