package com.hand.hdsp.quality.infra.measure.measure;

import com.hand.hdsp.quality.api.dto.WarningLevelDTO;
import com.hand.hdsp.quality.domain.entity.BatchResultBase;
import com.hand.hdsp.quality.domain.entity.BatchResultItem;
import com.hand.hdsp.quality.domain.entity.ItemTemplateSql;
import com.hand.hdsp.quality.domain.repository.ItemTemplateSqlRepository;
import com.hand.hdsp.quality.infra.constant.PlanConstant;
import com.hand.hdsp.quality.infra.dataobject.MeasureParamDO;
import com.hand.hdsp.quality.infra.measure.CheckItem;
import com.hand.hdsp.quality.infra.measure.Measure;
import com.hand.hdsp.quality.infra.measure.MeasureUtil;
import com.hand.hdsp.quality.infra.util.JsonUtils;
import com.hand.hdsp.quality.infra.vo.WarningLevelVO;
import org.apache.commons.collections4.CollectionUtils;
import org.hzero.boot.driver.app.service.DriverSessionService;
import org.hzero.starter.driver.core.session.DriverSession;

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
