package org.xdsp.quality.infra.measure.measure;

import com.alibaba.druid.DbType;
import org.apache.commons.collections4.CollectionUtils;
import org.hzero.boot.driver.app.service.DriverSessionService;
import org.hzero.starter.driver.core.session.DriverSession;
import org.xdsp.quality.api.dto.WarningLevelDTO;
import org.xdsp.quality.domain.entity.BatchResultBase;
import org.xdsp.quality.domain.entity.BatchResultItem;
import org.xdsp.quality.domain.entity.ItemTemplateSql;
import org.xdsp.quality.domain.repository.ItemTemplateSqlRepository;
import org.xdsp.quality.infra.constant.PlanConstant;
import org.xdsp.quality.infra.dataobject.MeasureParamDO;
import org.xdsp.quality.infra.measure.CheckItem;
import org.xdsp.quality.infra.measure.Measure;
import org.xdsp.quality.infra.measure.MeasureUtil;
import org.xdsp.quality.infra.util.JsonUtils;
import org.xdsp.quality.infra.vo.WarningLevelVO;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>一致性</p>
 *
 * @author feng.liu01@hand-china.com 2020-03-24 16:19:53
 */
@CheckItem(PlanConstant.CheckItem.CONSISTENCY)
public class ConsistencyMeasure implements Measure {

    private final ItemTemplateSqlRepository templateSqlRepository;
    private final DriverSessionService driverSessionService;

    public ConsistencyMeasure(ItemTemplateSqlRepository templateSqlRepository, DriverSessionService driverSessionService) {
        this.templateSqlRepository = templateSqlRepository;
        this.driverSessionService = driverSessionService;
    }

    @Override
    public BatchResultItem check(MeasureParamDO param) {
        Long tenantId = param.getTenantId();
        BatchResultBase batchResultBase = param.getBatchResultBase();
        BatchResultItem batchResultItem = param.getBatchResultItem();
        WarningLevelDTO warningLevelDTO = param.getWarningLevelList().get(0);

        // 查询要执行的SQL
        ItemTemplateSql itemTemplateSql = templateSqlRepository.selectSql(ItemTemplateSql.builder()
                .checkItem(PlanConstant.CheckItem.CONSISTENCY)
                .datasourceType(batchResultBase.getDatasourceType())
                .build());

        Map<String, String> variables = new HashMap<>(8);
        variables.put("table", batchResultBase.getPackageObjectName());
        variables.put("field", MeasureUtil.handleFieldName(param.getFieldName()));
        variables.put("checkField", MeasureUtil.handleFieldName(param.getCheckFieldName()));

        DriverSession driverSession = driverSessionService.getDriverSession(tenantId, param.getPluginDatasourceDTO().getDatasourceCode());
        String sql=MeasureUtil.replaceVariable(itemTemplateSql.getSqlContent(), variables, param.getWhereCondition());
        List<Map<String, Object>> response = driverSession.executeOneQuery(param.getSchema(),sql);
        if (DbType.hive.equals(driverSession.getDbType())&&CollectionUtils.isNotEmpty(response)) {
            //如果是hive，判断最后结果是不是包含hive-sql执行日志，如果包含，进行移除，避免日志对于结果的影响
            if (response.get(response.size() - 1).get("hive-sql") != null) {
                response.remove(response.size() - 1);
            }
        }

        if (CollectionUtils.isNotEmpty(response)) {
            batchResultItem.setWarningLevel(JsonUtils.object2Json(Collections.singleton(WarningLevelVO.builder()
                    .warningLevel(warningLevelDTO.getWarningLevel())
                    .build())));
//            PlanExceptionUtil.getPlanException(param, batchResultBase, sql, driverSession, warningLevelDTO);
            batchResultItem.setExceptionInfo("不满足一致性（规则字段组合相同时，校验字段均相同）");
        }

        return batchResultItem;
    }
}
