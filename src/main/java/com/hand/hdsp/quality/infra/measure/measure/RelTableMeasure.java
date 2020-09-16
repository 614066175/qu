package com.hand.hdsp.quality.infra.measure.measure;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

import com.hand.hdsp.driver.core.app.service.DriverSessionService;
import com.hand.hdsp.driver.core.app.service.session.DriverSession;
import com.hand.hdsp.quality.api.dto.RelationshipDTO;
import com.hand.hdsp.quality.api.dto.WarningLevelDTO;
import com.hand.hdsp.quality.domain.entity.BatchPlanRelTable;
import com.hand.hdsp.quality.domain.entity.BatchResultBase;
import com.hand.hdsp.quality.domain.entity.BatchResultItem;
import com.hand.hdsp.quality.domain.entity.ItemTemplateSql;
import com.hand.hdsp.quality.domain.repository.ItemTemplateSqlRepository;
import com.hand.hdsp.quality.infra.constant.ErrorCode;
import com.hand.hdsp.quality.infra.constant.PlanConstant;
import com.hand.hdsp.quality.infra.dataobject.MeasureParamDO;
import com.hand.hdsp.quality.infra.measure.CheckItem;
import com.hand.hdsp.quality.infra.measure.Measure;
import com.hand.hdsp.quality.infra.measure.MeasureUtil;
import com.hand.hdsp.quality.infra.util.JsonUtils;
import io.choerodon.core.exception.CommonException;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.hzero.core.base.BaseConstants;

/**
 * <p>表间关系</p>
 *
 * @author feng.liu01@hand-china.com 2020-03-24 16:19:53
 */
@CheckItem(PlanConstant.RuleType.TABLE_RELATION)
public class RelTableMeasure implements Measure {

    private final ItemTemplateSqlRepository templateSqlRepository;
    private final DriverSessionService driverSessionService;
    private static final String SQL = "SELECT count(*) count FROM %s source WHERE EXISTS (SELECT 1 FROM %s.%s rel WHERE %s) and %s";

    public RelTableMeasure(ItemTemplateSqlRepository templateSqlRepository, DriverSessionService driverSessionService) {
        this.templateSqlRepository = templateSqlRepository;
        this.driverSessionService = driverSessionService;
    }

    @Override
    public BatchResultItem check(MeasureParamDO param) {
        Long tenantId = param.getTenantId();
        BatchResultBase batchResultBase = param.getBatchResultBase();
        BatchPlanRelTable batchPlanRelTable = param.getBatchPlanRelTable();
        List<RelationshipDTO> relationshipDTOList = JsonUtils.json2Relationship(batchPlanRelTable.getRelationship());
        List<WarningLevelDTO> warningLevelList = JsonUtils.json2WarningLevel(batchPlanRelTable.getWarningLevel());
        BatchResultItem batchResultItem = param.getBatchResultItem();


        StringBuilder where = new StringBuilder();
        where.append(" 1 = 1 ");
        if (CollectionUtils.isNotEmpty(relationshipDTOList)) {
            for (RelationshipDTO relationshipDTO : relationshipDTOList) {
                where.append(" and source.").append(relationshipDTO.getSourceFieldName())
                        .append(relationshipDTO.getRelCode())
                        .append(" rel.").append(relationshipDTO.getRelFieldName());
            }
        }
        if (StringUtils.isNotBlank(batchPlanRelTable.getWhereCondition())) {
            where.append(" and ").append(batchPlanRelTable.getWhereCondition());
        }
        DriverSession driverSession = driverSessionService.getDriverSession(tenantId, param.getPluginDatasourceDTO().getDatasourceCode());
        List<Map<String, Object>> result = driverSession.executeOneQuery(param.getSchema(),
                String.format(SQL, batchResultBase.getPackageObjectName(), batchPlanRelTable.getRelSchema(),
                        batchPlanRelTable.getRelTableName(), where.toString(), Objects.isNull(batchResultBase.getWhereCondition()) ? "1=1" : batchPlanRelTable.getWhereCondition()));
        //获取表行数
        ItemTemplateSql itemTemplateSql = templateSqlRepository.selectSql(ItemTemplateSql.builder()
                .checkItem(PlanConstant.CheckItem.TABLE_LINE)
                .datasourceType(batchResultBase.getDatasourceType())
                .build());

        Map<String, String> variables = new HashMap<>(5);
        variables.put("table", batchPlanRelTable.getRelSchema() + "." + batchPlanRelTable.getRelTableName());

        List<Map<String, Object>> response = driverSession.executeOneQuery(param.getSchema(),
                MeasureUtil.replaceVariable(itemTemplateSql.getSqlContent(), variables, batchPlanRelTable.getWhereCondition()));
        if (response.size() != 1 || response.get(0).size() != 1) {
            throw new CommonException(ErrorCode.CHECK_ITEM_ONE_VALUE);
        }

        long dataCount = Long.parseLong(String.valueOf(response.get(0).values().toArray()[0]));

        //计算准确率
        BigDecimal a = new BigDecimal(String.valueOf(Optional.ofNullable(result.get(0).get("count")).orElse(result.get(0).get("COUNT"))));
        BigDecimal b = BigDecimal.valueOf(dataCount);
        BigDecimal rate = BigDecimal.ZERO;
        if (BigDecimal.ZERO.compareTo(b) != 0) {
            rate = a.divide(b, 2, RoundingMode.HALF_UP).multiply(new BigDecimal(100));
        }

        batchResultItem.setActualValue(rate.toString() + BaseConstants.Symbol.PERCENTAGE);
        batchResultItem.setCurrentValue(rate.toString());
        batchResultItem.setRelTableName(batchPlanRelTable.getRelTableName());

        for (WarningLevelDTO warningLevel : warningLevelList) {
            String startValue = warningLevel.getStartValue();
            String endValue = warningLevel.getEndValue();
            if (StringUtils.isBlank(startValue) && StringUtils.isBlank(endValue)) {
                throw new CommonException(ErrorCode.WARNING_LEVEL_RANGE_NOT_ALL_EMPTY);
            }
            boolean startResult = true;
            boolean endResult = true;
            if (StringUtils.isNotBlank(startValue)) {
                startResult = new BigDecimal(startValue).compareTo(rate) <= 0;
            }
            if (StringUtils.isNotBlank(endValue)) {
                endResult = new BigDecimal(endValue).compareTo(rate) >= 0;
            }
            if (startResult && endResult) {
                batchResultItem.setWarningLevel(warningLevel.getWarningLevel());
                batchResultItem.setExceptionInfo("准确率（目标表与源表匹配条数/源表总条数）在告警范围内");
            }
        }

        return batchResultItem;
    }
}
