package com.hand.hdsp.quality.infra.util;

import com.alibaba.druid.DbType;
import com.hand.hdsp.quality.api.dto.WarningLevelDTO;
import com.hand.hdsp.quality.domain.entity.BatchResultBase;
import com.hand.hdsp.quality.infra.constant.ErrorCode;
import com.hand.hdsp.quality.infra.dataobject.MeasureParamDO;
import io.choerodon.core.convertor.ApplicationContextHelper;
import io.choerodon.core.exception.CommonException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.util.Strings;
import org.hzero.boot.platform.profile.ProfileClient;
import org.hzero.starter.driver.core.session.DriverSession;
import org.springframework.context.ApplicationContext;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.Collectors;

import static com.hand.hdsp.quality.infra.constant.PlanConstant.ExceptionParam.*;
import static com.hand.hdsp.quality.infra.constant.PlanConstant.SqlType.SQL;

/**
 * <p>
 * description
 * </p>
 *
 * @author lgl 2020/12/29 14:03
 * @since 1.0
 */
@Slf4j
public class PlanExceptionUtil {

    public static Integer BATCH_NUMBER;

    public static final String COUNT_SQL = "select count (*) from (%s) a";

    public static MongoTemplate mongoTemplate;

    public static ProfileClient profileClient;

    public static final String EXCEPTION_NUMBER = "XQUA.EXCEPTION_NUMBER";

    static {
        ApplicationContext context = ApplicationContextHelper.getContext();
        mongoTemplate = context.getBean(MongoTemplate.class);
        profileClient = context.getBean(ProfileClient.class);
        String batchNumber = context.getEnvironment().resolvePlaceholders("${hdsp.quality.batch-number}");
        try {
            BATCH_NUMBER = Integer.parseInt(batchNumber);
        } catch (Exception e) {
            BATCH_NUMBER = 10000;
        }
    }


    /**
     * 根据base表名查询不符合标准的数据,将异常数据存入mongo
     *
     * @param param
     * @param batchResultBase
     * @param sql
     * @param driverSession
     * @param warningLevelDTO
     */
    public static void getPlanException(MeasureParamDO param, BatchResultBase batchResultBase, String sql, DriverSession driverSession, WarningLevelDTO warningLevelDTO) {
        //给配置维护，控制获取异常数据的数量
        String exceptionNumber = profileClient.getProfileValueByOptions(batchResultBase.getTenantId(), null, null, EXCEPTION_NUMBER);
        Long limit = null;
        log.info("exceptionNumber:" + exceptionNumber);
        try {
            if (StringUtils.isNotEmpty(exceptionNumber)) {
                if (Long.parseLong(exceptionNumber) <= 0) {
                    return;
                } else {
                    limit = Long.parseLong(exceptionNumber);
                }
            }
        } catch (Exception e) {
            //配置维护有问题，默认没有
            log.error("配置维护有问题，默认没有配置");
        }
        log.info("获取异常数据开始" + exceptionNumber);
        if (Strings.isEmpty(sql)) {
            throw new CommonException(ErrorCode.SQL_IS_EMPTY);
        }
        String tableName = batchResultBase.getPackageObjectName();
        //如果是sql类型，则
        if (SQL.equals(batchResultBase.getSqlType())) {
            //则使用定义的别名
            tableName = "sql_pack";
        }
        sql = String.format("select %s.* %s", tableName, sql.substring(sql.toLowerCase().indexOf("from")));
        //获取指定条数
        if (limit != null) {
            sql = driverSession.getPageSql(sql, PageRequest.of(0, Math.toIntExact(limit)));
        }
        log.info("获取异常数据sql：" + sql);
        List<Map<String, Object>> countMaps = driverSession.executeOneQuery(param.getSchema(), String.format(COUNT_SQL, sql));
        int count = Integer.parseInt(countMaps.get(0).values().toArray()[0].toString());
        if (count > BATCH_NUMBER) {
            int threadNumber = count % BATCH_NUMBER == 0 ? count / BATCH_NUMBER : count / BATCH_NUMBER + 1;
            ThreadPoolExecutor executor = CustomThreadPool.getExecutor();
            List<Future> futures = new ArrayList<>();
            for (int i = 0; i < threadNumber; i++) {
                int finalI = i;
                String finalSql = sql;
                Future<?> future = executor.submit(() -> getExceptionResult(finalI, param, batchResultBase, finalSql, driverSession, warningLevelDTO));
                futures.add(future);
            }
            for (Future future : futures) {
                try {
                    future.get();
                } catch (Exception e) {
                    //如果有一个线程失败，则删除此问题数据，并抛出异常
                    mongoTemplate.dropCollection(String.format("%d_%d", batchResultBase.getPlanBaseId(), batchResultBase.getResultBaseId()));
                    e.printStackTrace();
                    log.error("异常数据获取失败" + e.getMessage());
                    throw new CommonException("获取异常数据失败！请重新评估");
                }
            }
            log.info("异常数据获取结束");
        } else {
            //无需分页
            getExceptionResult(null, param, batchResultBase, sql, driverSession, warningLevelDTO);
            log.info("异常数据获取结束");
        }
    }

    private static void getExceptionResult(Integer pageNumber, MeasureParamDO param, BatchResultBase batchResultBase, String sql, DriverSession driverSession, WarningLevelDTO warningLevelDTO) {
        List<Map<String, Object>> exceptionMapList;
        if (pageNumber != null) {
            //进行分页
            Page<Map<String, Object>> maps = driverSession.executeOneQuery(param.getSchema(), sql, PageRequest.of(pageNumber, BATCH_NUMBER));
            exceptionMapList = maps.getContent();
        } else {
            exceptionMapList = driverSession.executeOneQuery(param.getSchema(), sql);
        }
        //避免分页查询返回的是不可修改的list
        exceptionMapList = new ArrayList<>(exceptionMapList);
        if (CollectionUtils.isNotEmpty(exceptionMapList)) {
            //如果是hive，则去除最后一个结果，最后一个结果是hive-sql执行日志，并非异常数据
            if (DbType.hive.equals(driverSession.getDbType())) {
                //判断最后一行是不是hive-sql的执行日志，如果是则移除，如果不是则不处理
                if (exceptionMapList.get(exceptionMapList.size() - 1).get("hive-sql") != null) {
                    exceptionMapList.remove(exceptionMapList.size() - 1);
                }
            }
            //每一条异常数据存上规则名和异常信息
            exceptionMapList.forEach(map -> {
                List<String> values = map.values().stream().map(String::valueOf).collect(Collectors.toList());
                //将所有的数据
                map.put(PK, String.join("-", values));
                map.put(PLAN_BASE_ID, batchResultBase.getPlanBaseId());
                map.put(RESULT_BASE_ID, batchResultBase.getResultBaseId());
                map.put(RULE_NAME, param.getBatchResultRuleDTO().getRuleName());
                map.put(WARNING_LEVEL, warningLevelDTO.getWarningLevel());
                //异常信息 name 数据长度 满足告警等级【大于5小于10】
                StringBuilder warningCondition = new StringBuilder();
                if (Strings.isNotEmpty(warningLevelDTO.getStartValue())) {
                    warningCondition.append(String.format("大于%s", warningLevelDTO.getStartValue()));
                }
                if (Strings.isNotEmpty(warningLevelDTO.getEndValue())) {
                    warningCondition.append(String.format("小于%s", warningLevelDTO.getEndValue()));
                }
                if (Strings.isNotEmpty(warningLevelDTO.getExpectedValue())) {
                    if ("EQUAL".equals(warningLevelDTO.getCompareSymbol())) {
                        warningCondition.append(String.format("等于%s", warningLevelDTO.getExpectedValue()));
                    } else {
                        warningCondition.append(String.format("不等于%s", warningLevelDTO.getExpectedValue()));
                    }
                }
                String exception = String.format("【%s】 %s 满足告警条件 %s",
                        param.getFieldName(),
                        String.format("%s %s", param.getCheckItem(), param.getCountType()),
                        Strings.isNotEmpty(warningCondition) ? String.format("【%s】", warningCondition) : "");
                map.put(EXCEPTION_INFO, String.format("%s(%s)", warningLevelDTO.getWarningLevel(), exception));
            });
        }
        //collection 使用质检项Id_结果Id
        mongoTemplate.insert(exceptionMapList, String.format("%d_%d", batchResultBase.getPlanBaseId(), batchResultBase.getResultBaseId()));
    }


}
