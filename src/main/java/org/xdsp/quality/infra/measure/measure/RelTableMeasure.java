package org.xdsp.quality.infra.measure.measure;

import com.alibaba.druid.DbType;
import io.choerodon.core.exception.CommonException;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.util.Strings;
import org.hzero.boot.driver.app.service.DriverSessionService;
import org.hzero.boot.platform.lov.adapter.LovAdapter;
import org.hzero.boot.platform.lov.dto.LovValueDTO;
import org.hzero.core.base.BaseConstants;
import org.hzero.starter.driver.core.session.DriverSession;
import org.xdsp.quality.api.dto.RelationshipDTO;
import org.xdsp.quality.api.dto.TableRelCheckDTO;
import org.xdsp.quality.api.dto.WarningLevelDTO;
import org.xdsp.quality.domain.entity.BatchPlanRelTable;
import org.xdsp.quality.domain.entity.BatchResultBase;
import org.xdsp.quality.domain.entity.BatchResultItem;
import org.xdsp.quality.domain.entity.ItemTemplateSql;
import org.xdsp.quality.domain.repository.ItemTemplateSqlRepository;
import org.xdsp.quality.infra.constant.ErrorCode;
import org.xdsp.quality.infra.constant.PlanConstant;
import org.xdsp.quality.infra.dataobject.MeasureParamDO;
import org.xdsp.quality.infra.measure.CheckItem;
import org.xdsp.quality.infra.measure.Measure;
import org.xdsp.quality.infra.measure.MeasureUtil;
import org.xdsp.quality.infra.util.JsonUtils;
import org.xdsp.quality.infra.vo.WarningLevelVO;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

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
    //private static final String ACCURACY_RATE_SQL = "SELECT count(*) count FROM %s base WHERE EXISTS (SELECT 1 FROM %s.%s rel WHERE %s) and %s";
    private static final String CALCULATED_VALUE_SQL = "select count(*) COUNT from (%s) rel  inner join (%s) base on %s";
    private static final String ORIGIN_SQL_TEMPLATE = "select %s from %s";
    private static final String OTHER_SQL_TEMPLATE = "select %s from %s %s";
    private static final String JOIN_TEMPLATE_SQL = "select %s from (%s) a join (%s) b on %s";
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
            //获取sql模板
            ItemTemplateSql accuracyItemTemplateSql = templateSqlRepository.selectSql(ItemTemplateSql.builder()
                    .checkItem(PlanConstant.CheckItem.ACCURACY_RATE)
                    .datasourceType(batchResultBase.getDatasourceType())
                    .build());
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
                    String.format(accuracyItemTemplateSql.getSqlContent(), batchResultBase.getPackageObjectName(), batchPlanRelTable.getRelSchema(),
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
            if (!DbType.hive.equals(driverSession.getDbType())) {
                if (response.size() != 1 || response.get(0).size() != 1) {
                    throw new CommonException(ErrorCode.CHECK_ITEM_ONE_VALUE);
                }
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
                    batchResultItem.setWarningLevel(JsonUtils.object2Json(
                            Collections.singletonList(WarningLevelVO.builder()
                                    .warningLevel(warningLevel.getWarningLevel())
                                    .build())));
                    batchResultItem.setExceptionInfo("准确率（目标表与源表匹配条数/源表总条数）在告警范围内");
                }
            }
        }
        //计算值比较
        else if (PlanConstant.CheckItem.CALCULATED_VALUE.equals(param.getCheckItem())) {
            String tableRelCheck = batchPlanRelTable.getTableRelCheck();
            if (Strings.isNotEmpty(tableRelCheck)) {
                StringBuilder onCondition = new StringBuilder();
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
                // 存放源表sql：聚合函数类型为Origin的sql，及聚合函数类型为其他的sql
                List<String> relSqlList = new ArrayList<>();
                List<String> baseSqlList = new ArrayList<>();
                // 存放源表聚合函数为origin的列
                List<String> relOriginFieldNameList = new ArrayList<>(relList);
                // 存放源表聚合函数为其他的列
                List<String> relOtherFieldNameList = new ArrayList<>(relList);
                List<String> baseOriginFieldNameList = new ArrayList<>(baseList);
                List<String> baseOtherFieldNameList = new ArrayList<>(baseList);
                //既有origin，又有其他聚合函数 需要join处理
                for (int i = 0; i < tableRelCheckDTOList.size(); i++) {
                    TableRelCheckDTO tableRelCheckDTO = tableRelCheckDTOList.get(i);
                    // 1. 根据聚合函数类型分组，把列名分组
                    if ("ORIGIN".equals(tableRelCheckDTO.getRelFunction())) {
                        relOriginFieldNameList.add(MeasureUtil.handleFunc(tableRelCheckDTO.getRelFunction(), tableRelCheckDTO.getRelFieldName(), batchResultBase.getDatasourceType(), i));
                    } else {
                        relOtherFieldNameList.add(MeasureUtil.handleFunc(tableRelCheckDTO.getRelFunction(), tableRelCheckDTO.getRelFieldName(), batchResultBase.getDatasourceType(), i));
                    }
                    if ("ORIGIN".equals(tableRelCheckDTO.getBaseFunction())) {
                        baseOriginFieldNameList.add(MeasureUtil.handleFunc(tableRelCheckDTO.getBaseFunction(), tableRelCheckDTO.getBaseFieldName(), batchResultBase.getDatasourceType(), i));
                    } else {
                        baseOtherFieldNameList.add(MeasureUtil.handleFunc(tableRelCheckDTO.getBaseFunction(), tableRelCheckDTO.getBaseFieldName(), batchResultBase.getDatasourceType(), i));
                    }
                    onCondition.append(String.format(" and rel.%s %s base.%s",
                        String.format("function%d", i),
                        tableRelCheckDTOList.get(i).getRelCode(),
                        String.format("function%d", i)));
                }
                // 分别处理源表和目标表的sql连接
                String composeRelSql = processRelSql(batchPlanRelTable, tableRelCheckDTOList, relList, whereCondition, relGroupBy, relSqlList, relOriginFieldNameList, relOtherFieldNameList);
                String composeBaseSql = processBaseSql(batchResultBase, tableRelCheckDTOList, baseList, baseGroupBy, baseSqlList, baseOriginFieldNameList, baseOtherFieldNameList);
                // 去除多余的 and
                onCondition.replace(0, 4, "");
                DriverSession driverSession = driverSessionService.getDriverSession(tenantId, param.getPluginDatasourceDTO().getDatasourceCode());
                String sql = String.format(CALCULATED_VALUE_SQL,
                        composeRelSql,
                        composeBaseSql,
                        onCondition
                );
                List<Map<String, Object>> result = driverSession.executeOneQuery(param.getSchema(), sql);
                if (CollectionUtils.isNotEmpty(result) && Long.parseLong(result.get(0).values().toArray()[0].toString()) != 0) {
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
                            .levelCount(Long.parseLong(result.get(0).values().toArray()[0].toString()))
                            .build();
                    batchResultItem.setWarningLevel(JsonUtils.object2Json(Collections.singletonList(warningLevelVO)));
                    batchResultItem.setExceptionInfo("数据满足计算值比较告警");
                }
            }
        }
        return batchResultItem;
    }

    private String processBaseSql(BatchResultBase batchResultBase, List<TableRelCheckDTO> tableRelCheckDTOList, List<String> baseList, StringBuilder baseGroupBy, List<String> baseSqlList, List<String> baseOriginFieldNameList, List<String> baseOtherFieldNameList) {
        baseSqlList.add(String.format(ORIGIN_SQL_TEMPLATE,
                Strings.join(baseOriginFieldNameList, ','),
                batchResultBase.getPackageObjectName()
        ));
        baseSqlList.add(String.format(OTHER_SQL_TEMPLATE,
                Strings.join(baseOtherFieldNameList, ','),
                batchResultBase.getPackageObjectName(),
                baseGroupBy
        ));

        List<String> afterJoinBaseNames = new ArrayList<>(baseList);
        afterJoinBaseNames = afterJoinBaseNames.stream().map(name -> String.format("a.%s", name)).collect(Collectors.toList());
        for (int i = 0; i < tableRelCheckDTOList.size(); i++) {
            afterJoinBaseNames.add(String.format("function%d", i));
        }

        String baseJoinConditon = baseList.stream().map(name -> String.format(" a.%s = b.%s ",name, name)).collect(Collectors.joining("and"));

        String composeBaseSql = String.format(JOIN_TEMPLATE_SQL,
                Strings.join(afterJoinBaseNames, ','),
                baseSqlList.get(0),
                baseSqlList.get(1),
                baseJoinConditon
        );
        return composeBaseSql;
    }

    private String processRelSql(BatchPlanRelTable batchPlanRelTable, List<TableRelCheckDTO> tableRelCheckDTOList, List<String> relList, String whereCondition, StringBuilder relGroupBy, List<String> relSqlList, List<String> relOriginFieldNameList, List<String> relOtherFieldNameList) {
        // 2.根据列名 拼接sql: 聚合函数为属性值的sql不加group by
        relSqlList.add(String.format(ORIGIN_SQL_TEMPLATE,
                Strings.join(relOriginFieldNameList, ','),
                String.format("%s.%s",  batchPlanRelTable.getRelSchema(), batchPlanRelTable.getRelTableName())
                ));
        // 2.根据列名 拼接sql: 聚合函数为其他的sql需要加group by
        relSqlList.add(String.format(OTHER_SQL_TEMPLATE,
                Strings.join(relOtherFieldNameList, ','),
                String.format("%s.%s",  batchPlanRelTable.getRelSchema(), batchPlanRelTable.getRelTableName()),
                String.format(" where %s %s", whereCondition, relGroupBy)
                ));
        //拼接连接后的 列名
        List<String> afterJoinRelNames = new ArrayList<>(relList);
        afterJoinRelNames = afterJoinRelNames.stream().map(name -> String.format("a.%s", name)).collect(Collectors.toList());
        for (int i = 0; i < tableRelCheckDTOList.size(); i++) {
            afterJoinRelNames.add(String.format("function%d", i));
        }
        String relJoinConditon = relList.stream().map(name -> String.format(" a.%s = b.%s ",name, name)).collect(Collectors.joining("and"));
        // 3. 连接拼接好的sql
        String composeRelSql = String.format(JOIN_TEMPLATE_SQL,
                Strings.join(afterJoinRelNames, ','),
                relSqlList.get(0),
                relSqlList.get(1),
                relJoinConditon
        );
        return composeRelSql;
    }
}
