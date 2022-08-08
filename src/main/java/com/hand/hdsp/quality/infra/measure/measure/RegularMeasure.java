package com.hand.hdsp.quality.infra.measure.measure;

import com.alibaba.druid.DbType;
import com.hand.hdsp.quality.domain.entity.BatchResultBase;
import com.hand.hdsp.quality.domain.entity.BatchResultItem;
import com.hand.hdsp.quality.domain.entity.ItemTemplateSql;
import com.hand.hdsp.quality.domain.repository.ItemTemplateSqlRepository;
import com.hand.hdsp.quality.infra.constant.PlanConstant;
import com.hand.hdsp.quality.infra.dataobject.MeasureParamDO;
import com.hand.hdsp.quality.infra.dataobject.MeasureResultDO;
import com.hand.hdsp.quality.infra.measure.CheckItem;
import com.hand.hdsp.quality.infra.measure.Measure;
import com.hand.hdsp.quality.infra.measure.MeasureUtil;
import com.hand.hdsp.quality.infra.util.JsonUtils;
import com.hand.hdsp.quality.infra.util.PlanExceptionUtil;
import com.hand.hdsp.quality.infra.vo.WarningLevelVO;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.util.Strings;
import org.hzero.boot.driver.app.service.DriverSessionService;
import org.hzero.core.base.BaseConstants;
import org.hzero.starter.driver.core.infra.meta.PrimaryKey;
import org.hzero.starter.driver.core.session.DriverSession;

import java.math.BigDecimal;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.hand.hdsp.quality.infra.constant.PlanConstant.ExceptionParam.*;

/**
 * <p>正则表达式</p>
 *
 * @author feng.liu01@hand-china.com 2020-03-24 16:19:53
 */
@CheckItem(PlanConstant.CheckWay.REGULAR)
public class RegularMeasure implements Measure {

    private final ItemTemplateSqlRepository templateSqlRepository;
    private final DriverSessionService driverSessionService;

    public RegularMeasure(ItemTemplateSqlRepository templateSqlRepository, DriverSessionService driverSessionService) {
        this.templateSqlRepository = templateSqlRepository;
        this.driverSessionService = driverSessionService;
    }

    @Override
    public BatchResultItem check(MeasureParamDO param) {
        Long tenantId = param.getTenantId();
        BatchResultBase batchResultBase = param.getBatchResultBase();
        BatchResultItem batchResultItem = param.getBatchResultItem();
        DriverSession driverSession = driverSessionService.getDriverSession(tenantId, param.getPluginDatasourceDTO().getDatasourceCode());

        // 查询要执行的SQL
        ItemTemplateSql itemTemplateSql = templateSqlRepository.selectSql(ItemTemplateSql.builder()
                .checkItem(PlanConstant.CheckWay.REGULAR)
                .datasourceType(batchResultBase.getDatasourceType())
                .build());

        // 用执行SQL的方式校验
        if (PlanConstant.TemplateSqlTag.SQL.equals(itemTemplateSql.getTag())) {
            Map<String, String> variables = new HashMap<>(8);
            variables.put("table", batchResultBase.getPackageObjectName());
            variables.put("field", MeasureUtil.handleFieldName(param.getFieldName()));
            variables.put("regexp", param.getRegularExpression());
            String sql = MeasureUtil.replaceVariable(itemTemplateSql.getSqlContent(), variables, param.getWhereCondition());
            List<Map<String, Object>> response = driverSession.executeOneQuery(param.getSchema(), sql);

            String value = response.get(0).values().toArray(new String[0])[0];
            BigDecimal result = new BigDecimal(value);
            if (result.compareTo(BigDecimal.ONE) != 0) {
                batchResultItem.setWarningLevel(JsonUtils.object2Json(
                        Collections.singletonList(
                                WarningLevelVO.builder()
                                        .warningLevel(param.getWarningLevelList().get(0).getWarningLevel())
                                        .build()
                        )));
                batchResultItem.setExceptionInfo("不满足正则表达式");
            }
        }
        // 查询出数据在Java里校验
        else if (PlanConstant.TemplateSqlTag.JAVA.equals(itemTemplateSql.getTag())) {
            Map<String, String> variables = new HashMap<>(8);
            variables.put("table", batchResultBase.getPackageObjectName());
            variables.put("field", MeasureUtil.handleFieldName(param.getFieldName()));
            variables.put("size", PlanConstant.DEFAULT_SIZE + "");

            Pattern pattern = Pattern.compile(param.getRegularExpression());

            boolean successFlag = true;
            for (int i = 0; ; i++) {
                int start = i * PlanConstant.DEFAULT_SIZE;
                variables.put("start", start + "");
                String sql = MeasureUtil.replaceVariable(itemTemplateSql.getSqlContent(), variables, param.getWhereCondition());
                List<Map<String, Object>> maps = driverSession.executeOneQuery(param.getSchema(), sql);
                List<MeasureResultDO> list = new ArrayList<>();
                //如果是hive类型，并且结果获取到了hive-sql日志，进行排除
                if(DbType.hive.equals(driverSession.getDbType())){
                    //判断最后一行是不是hive-sql的执行日志，如果是则移除，如果不是则不处理
                    if (maps.get(maps.size() - 1).get("hive-sql") != null) {
                        maps.remove(maps.size() - 1);
                    }
                }
                maps.forEach((map) -> map.forEach((k, v) -> list.add(new MeasureResultDO(String.valueOf(v)))));
                List<String> noMatchList=new ArrayList<>();
                long count = 1;
                for (MeasureResultDO measureResultDO : list) {
                    if (!pattern.matcher(measureResultDO.getResult()).find()) {
                        noMatchList.add(String.format("'%s'",measureResultDO.getResult()));
                        batchResultItem.setActualValue(measureResultDO.getResult());
                        batchResultItem.setWarningLevel(JsonUtils.object2Json(
                                Collections.singletonList(
                                        WarningLevelVO.builder()
                                                .warningLevel(param.getWarningLevelList().get(0).getWarningLevel())
                                                .levelCount(count++)
                                                .build()
                                )
                        ));
                        batchResultItem.setExceptionInfo("不满足正则表达式");
                        successFlag = false;
//                        break;
                    }
                }
                if(CollectionUtils.isNotEmpty(noMatchList)){
                    String noMatchCondition;
                    // 防止mysql表字符集为utf8mb4_general_ci时，不区分大小写的情况
                    if (DbType.mysql.equals(driverSession.getDbType())) {
                        noMatchCondition = String.format("BINARY %s in (%s)",
                                MeasureUtil.handleFieldName(param.getFieldName()),
                                Strings.join(noMatchList, ',')
                        );
                    } else {
                        noMatchCondition = String.format("%s in (%s)",
                                MeasureUtil.handleFieldName(param.getFieldName()),
                                Strings.join(noMatchList, ',')
                        );
                    }
                    //修改where条件
                    if(Strings.isEmpty(param.getWhereCondition())){
                        param.setWhereCondition(noMatchCondition);
                    }else{
                        param.setWhereCondition(String.format("%s and %s",
                                param.getWhereCondition(),
                                noMatchCondition
                        ));
                    }
//                    String noMacthSql = MeasureUtil.replaceVariable(itemTemplateSql.getSqlContent(), variables, param.getWhereCondition());
                    //获取不符合正则的异常数据
                    String newSql = String.format("select %s.* from %s where 1=1 and %s ",
                            batchResultBase.getPackageObjectName(),
                            batchResultBase.getPackageObjectName(),
                            param.getWhereCondition()
                    );
                    param.getWarningLevelList().forEach(warningLevelDTO -> {
                        PlanExceptionUtil.getPlanException(param, batchResultBase, newSql, driverSession, warningLevelDTO);
                    });
                    List<PrimaryKey> primaryKeys = driverSession.tablePk(param.getSchema(), batchResultBase.getPackageObjectName());
                    List<Map<String, Object>> exceptionMapList = driverSession.executeOneQuery(param.getSchema(), newSql);
                    if (CollectionUtils.isNotEmpty(exceptionMapList)) {
                        exceptionMapList.forEach(map -> {
                            if (CollectionUtils.isNotEmpty(primaryKeys)) {
                                Set<String> columns = map.keySet();
                                List<String> columnPkList = primaryKeys.stream().map(PrimaryKey::getColumnName).collect(Collectors.toList());
                                List<String> keys = columns.stream().filter(columnPkList::contains).collect(Collectors.toList());
                                List<String> values = keys.stream().map(key -> String.valueOf(map.get(key))).collect(Collectors.toList());
                                map.put(PK, String.join("-", values));
                            }else{
                                List<String> values = map.values().stream().map(String::valueOf).collect(Collectors.toList());
                                //将所有的数据
                                map.put(PK,String.join("-",values));
                            }
                            map.put(RULE_NAME,param.getBatchResultRuleDTO().getRuleName());
                            map.put(WARNING_LEVEL,param.getWarningLevelList().get(0).getWarningLevel());
                            String exceptionInfo = String.format("【%s】 不满足正则表达式【%s】",
                                    param.getFieldName(),
                                    param.getRegularExpression());
                            map.put(EXCEPTION_INFO, String.format("%s(%s)",param.getWarningLevelList().get(0).getWarningLevel(),exceptionInfo));
                        });
                    }
                    param.setExceptionMapList(exceptionMapList);
                }

                //当成功标记为false 或者 查询出来的数据量小于每页大小时（即已到最后一页了）退出
                if (!successFlag || list.size() < PlanConstant.DEFAULT_SIZE) {
                    break;
                }
            }

        }

        return batchResultItem;
    }
}
