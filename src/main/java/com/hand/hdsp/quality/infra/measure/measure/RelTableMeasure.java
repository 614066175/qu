package com.hand.hdsp.quality.infra.measure.measure;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

import com.hand.hdsp.quality.api.dto.RelationshipDTO;
import com.hand.hdsp.quality.api.dto.TableRelCheckDTO;
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
import com.hand.hdsp.quality.infra.vo.WarningLevelVO;
import io.choerodon.core.exception.CommonException;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.util.Strings;
import org.hzero.boot.driver.app.service.DriverSessionService;
import org.hzero.boot.platform.lov.adapter.LovAdapter;
import org.hzero.boot.platform.lov.dto.LovValueDTO;
import org.hzero.core.base.BaseConstants;
import org.hzero.starter.driver.core.session.DriverSession;

/**
 * <p>表间关系</p>
 *
 * @author feng.liu01@hand-china.com 2020-03-24 16:19:53
 */
@CheckItem(PlanConstant.RuleType.TABLE_RELATION)
public class RelTableMeasure implements Measure {

    private static final String COUNT = "COUNT";
    private final ItemTemplateSqlRepository templateSqlRepository;
    private final DriverSessionService driverSessionService;
    private static final String ACCURACY_RATE_SQL = "SELECT count(*) count FROM %s base WHERE EXISTS (SELECT 1 FROM %s.%s rel WHERE %s) and %s";
    private static final String CALCULATED_VALUE_SQL = "select count(*) COUNT from (select %s from %s.%s where %s) rel  inner join (select %s from %s) base on %s";
    private final LovAdapter lovAdapter;

    public RelTableMeasure(ItemTemplateSqlRepository templateSqlRepository, DriverSessionService driverSessionService, LovAdapter lovAdapter) {
        this.templateSqlRepository = templateSqlRepository;
        this.driverSessionService = driverSessionService;
        this.lovAdapter = lovAdapter;
    }

    @Override
    public BatchResultItem check(MeasureParamDO param) {
        Long tenantId = param.getTenantId();
        BatchResultBase batchResultBase = param.getBatchResultBase();
        BatchPlanRelTable batchPlanRelTable = param.getBatchPlanRelTable();
        List<RelationshipDTO> relationshipDTOList = JsonUtils.json2Relationship(batchPlanRelTable.getRelationship());
        List<WarningLevelDTO> warningLevelList = JsonUtils.json2WarningLevel(batchPlanRelTable.getWarningLevel());
        BatchResultItem batchResultItem = param.getBatchResultItem();
        if (PlanConstant.CheckItem.ACCURACY_RATE.equals(param.getCheckItem())) {
            StringBuilder where = new StringBuilder();
            where.append(" 1 = 1 ");
            if (CollectionUtils.isNotEmpty(relationshipDTOList)) {
                for (RelationshipDTO relationshipDTO : relationshipDTOList) {
                    where.append(" and base.").append(relationshipDTO.getBaseFieldName())
                            .append(relationshipDTO.getRelCode())
                            .append(" rel.").append(relationshipDTO.getRelFieldName());
                }
            }
            if (StringUtils.isNotBlank(batchPlanRelTable.getWhereCondition())) {
                where.append(" and ").append(batchPlanRelTable.getWhereCondition());
            }
            DriverSession driverSession = driverSessionService.getDriverSession(tenantId, param.getPluginDatasourceDTO().getDatasourceCode());
            List<Map<String, Object>> result = driverSession.executeOneQuery(param.getSchema(),
                    String.format(ACCURACY_RATE_SQL, batchResultBase.getPackageObjectName(), batchPlanRelTable.getRelSchema(),
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
                    batchResultItem.setWarningLevel(JsonUtils.object2Json(WarningLevelVO.builder()
                            .warningLevel(warningLevel.getWarningLevel())
                            .build()));
                    batchResultItem.setExceptionInfo("准确率（目标表与源表匹配条数/源表总条数）在告警范围内");
                }
            }
        }
        //计算值比较
        else if (PlanConstant.CheckItem.CALCULATED_VALUE.equals(param.getCheckItem())) {
            String tableRelCheck = batchPlanRelTable.getTableRelCheck();
            if (Strings.isNotEmpty(tableRelCheck)) {
                StringBuilder onCondition = new StringBuilder();
                onCondition.append("1=1");
                List<TableRelCheckDTO> tableRelCheckDTOList = JsonUtils.json2TableRelCheck(tableRelCheck);
                List<String> relList = new ArrayList<>();
                List<String> baseList = new ArrayList<>();
                String whereCondition = Optional.ofNullable(batchPlanRelTable.getWhereCondition()).orElse("1=1");
                StringBuilder relGroupBy = new StringBuilder();
                StringBuilder baseGroupBy = new StringBuilder();
                relationshipDTOList.forEach(relationshipDTO -> {
                    relList.add(relationshipDTO.getRelFieldName());
                    baseList.add(relationshipDTO.getBaseFieldName());
                    onCondition.append(String.format(" and rel.%s %s base.%s",
                            relationshipDTO.getRelFieldName(),
                            relationshipDTO.getRelCode(),
                            relationshipDTO.getBaseFieldName()));
                });
                if (CollectionUtils.isNotEmpty(relList) && CollectionUtils.isNotEmpty(baseList)) {
                    relGroupBy.append("group by ").append(Strings.join(relList, ','));
                    baseGroupBy.append("group by ").append(Strings.join(baseList, ','));
                }
                for (int i = 0; i < tableRelCheckDTOList.size(); i++) {
                    relList.add(String.format("%s(%s) as %s",
                            tableRelCheckDTOList.get(i).getRelFunction(),
                            tableRelCheckDTOList.get(i).getRelFieldName(),
                            String.format("function%d", i)));
                    baseList.add(String.format("%s(%s) as %s",
                            tableRelCheckDTOList.get(i).getBaseFunction(),
                            tableRelCheckDTOList.get(i).getBaseFieldName(),
                            String.format("function%d", i)));
                    onCondition.append(String.format(" and rel.%s %s base.%s",
                            String.format("function%d", i),
                            tableRelCheckDTOList.get(i).getRelCode(),
                            String.format("function%d", i)));
                }
                DriverSession driverSession = driverSessionService.getDriverSession(tenantId, param.getPluginDatasourceDTO().getDatasourceCode());
                String sql = String.format(CALCULATED_VALUE_SQL,
                        Strings.join(relList, ','),
                        batchPlanRelTable.getRelSchema(),
                        batchPlanRelTable.getRelTableName(),
                        String.format("%s %s", whereCondition, relGroupBy),
                        Strings.join(baseList, ','),
                        String.format("%s %s", batchResultBase.getPackageObjectName(), baseGroupBy),
                        onCondition
                );
                List<Map<String, Object>> result = driverSession.executeOneQuery(param.getSchema(), sql);
                if (CollectionUtils.isNotEmpty(result) && (long) result.get(0).get(COUNT) != 0) {
                    List<WarningLevelDTO> warningLevelDTOList = JsonUtils.json2WarningLevel(batchPlanRelTable.getWarningLevel());
                    //取高等级告警
                    String warningLevel = "";
                    if (CollectionUtils.isNotEmpty(warningLevelDTOList)) {
                        if (warningLevelDTOList.size() == 1) {
                            warningLevel = warningLevelDTOList.get(0).getWarningLevel();
                        } else {
                            List<LovValueDTO> lovValueDTOList = lovAdapter.queryLovValue(warningLevelDTOList.get(0).getLovCode(), tenantId);
                            List<String> sortedWarningLevelList = lovValueDTOList.stream()
                                    .sorted(Comparator.comparing(LovValueDTO::getOrderSeq))
                                    .map(LovValueDTO::getValue)
                                    .collect(Collectors.toList());
                            if (CollectionUtils.isEmpty(sortedWarningLevelList)) {
                                throw new CommonException(ErrorCode.LOV_CODE_NOT_EXIST);
                            }
                            for (WarningLevelDTO warningLevelDTO : warningLevelDTOList) {
                                if (Strings.isEmpty(warningLevel)) {
                                    warningLevel = warningLevelDTO.getWarningLevel();
                                    continue;
                                }
                                if (sortedWarningLevelList.indexOf(warningLevelDTO.getWarningLevel())
                                        < sortedWarningLevelList.indexOf(warningLevel)) {
                                    warningLevel = warningLevelDTO.getWarningLevel();
                                }
                            }
                        }
                    }
                    WarningLevelVO warningLevelVO = WarningLevelVO.builder()
                            .warningLevel(warningLevel)
                            .levelCount((long) result.get(0).get(COUNT))
                            .build();
                    batchResultItem.setWarningLevel(JsonUtils.object2Json(Collections.singletonList(warningLevelVO)));
                    batchResultItem.setExceptionInfo("数据满足计算值比较告警");
                }
            }
        }
        return batchResultItem;
    }
}
