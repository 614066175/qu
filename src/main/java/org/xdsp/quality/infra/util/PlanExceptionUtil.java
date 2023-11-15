package org.xdsp.quality.infra.util;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import io.choerodon.core.convertor.ApplicationContextHelper;
import io.choerodon.core.exception.CommonException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.util.Strings;
import org.hzero.boot.driver.app.service.DriverSessionService;
import org.hzero.boot.platform.lov.adapter.LovAdapter;
import org.hzero.core.base.BaseConstants;
import org.hzero.core.redis.RedisHelper;
import org.hzero.starter.driver.core.infra.util.JsonUtil;
import org.hzero.starter.driver.core.session.DriverSession;
import org.springframework.context.ApplicationContext;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.xdsp.core.profile.XdspProfileClient;
import org.xdsp.quality.api.dto.WarningLevelDTO;
import org.xdsp.quality.domain.entity.BatchResultBase;
import org.xdsp.quality.infra.constant.ErrorCode;
import org.xdsp.quality.infra.consumer.ExceptionDataConsumer;
import org.xdsp.quality.infra.dataobject.MeasureParamDO;
import org.xdsp.quality.infra.message.MessageDTO;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static org.xdsp.quality.infra.constant.PlanConstant.ExceptionParam.*;
import static org.xdsp.quality.infra.constant.PlanConstant.SqlType.SQL;
import static org.xdsp.quality.infra.constant.PlanExceptionConstants.*;

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

    public static final String COUNT_SQL = "select count(*) from (%s) a";

    public static MongoTemplate mongoTemplate;

    public static XdspProfileClient profileClient;
    public static RedisHelper redisHelper;
    public static DriverSessionService driverSessionService;
    public static LovAdapter lovAdapter;

    public static final String WARNING_LEVEL_LOV = "XQUA.WARNING_LEVEL";

    public static final String CHECK_ITEM_LOV = "XQUA.CHECK_ITEM";

    public static final String COUNT_TYPE_LOV = "XQUA.COUNT_TYPE";

    public static final String EXCEPTION_NUMBER = "XQUA.EXCEPTION_NUMBER";
    public static final String PROBLEM_BATCH_NUMBER = "XQUA.PROBLEM_BATCH_NUMBER";
    public static String messageKey;
    public static ApplicationContext context = ApplicationContextHelper.getContext();

    static {
        mongoTemplate = context.getBean(MongoTemplate.class);
        profileClient = context.getBean(XdspProfileClient.class);
        redisHelper = context.getBean(RedisHelper.class);
        driverSessionService = context.getBean(DriverSessionService.class);
        lovAdapter = context.getBean(LovAdapter.class);
        String messagekeyConfig = context.getEnvironment().resolvePlaceholders("${xdsp.quality.message-key}");
        if (StringUtils.isEmpty(messagekeyConfig)) {
            messageKey = "xdsp:quality:exception-message";
        } else {
            messageKey = messagekeyConfig;
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
        long start = System.currentTimeMillis();
        //给配置维护，控制获取异常数据的数量
        String exceptionNumber = profileClient.getProfileValue(batchResultBase.getTenantId(), batchResultBase.getProjectId(), EXCEPTION_NUMBER);
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
        //获取异常数据sql处理，sql解析来做
        //处理最外层的SQLSelectItem
        try {
            List<SQLStatement> sqlStatements = SQLUtils.parseStatements(sql, driverSession.getDbType());
            ExceptionSqlVisitor exceptionSqlVisitor = new ExceptionSqlVisitor();
            //设置为表名
            exceptionSqlVisitor.setOwnerName(tableName);
            SQLStatement sqlStatement = sqlStatements.get(0);
            sqlStatement.accept(exceptionSqlVisitor);
            sql = SQLUtils.toSQLString(sqlStatement);
        } catch (Exception e) {
            log.error("sql解析错误，使用字符截断方式处理", e);
            sql = String.format("select %s.* %s", tableName, sql.substring(sql.toLowerCase().indexOf("from")));
        }

        //获取指定条数
        if (limit != null) {
            sql = driverSession.getPageSql(sql, PageRequest.of(0, Math.toIntExact(limit)));
        }
        log.info("获取异常数据sql：" + sql);
        List<Map<String, Object>> countMaps = driverSession.executeOneQuery(param.getSchema(), String.format(COUNT_SQL, sql));
        int count = Integer.parseInt(countMaps.get(0).values().toArray()[0].toString());
        log.info("异常数据量总量{}", count);
        //1.如果异常数据量小于单个批次量，则无需分批，直接执行
        //2.如果大于批次量，按照批次大小，拆分sql，发送给redis。服务阻塞消费redis中的key，当数据量过大，线程数过多，达到性能瓶颈时，可以水平扩容服务实例来增加并行度
        //3.通过计数器来控制线程执行结果，每个服务定义固定可用线程数，避免消息被一个服务过度消费，线程资源不能得到合理利用

        String batchNumberConfig = profileClient.getProfileValue(batchResultBase.getTenantId(), batchResultBase.getProjectId(), PROBLEM_BATCH_NUMBER);
        int batchNumber;
        //如果没有配置，默认为10000L
        if (Strings.isEmpty(batchNumberConfig)) {
            batchNumber = 10000;
        } else {
            try {
                batchNumber = Integer.parseInt(batchNumberConfig);
            } catch (Exception e) {
                batchNumber = 10000;
            }
        }

        //根据数据量进行一个处理，如果数据量小于一个单个批次数量，直接执行。
        if (count > batchNumber) {
            //根据批次大小获取要分多少页
            int pageNumber = count % batchNumber == 0 ? count / batchNumber : count / batchNumber + 1;
            //发送消息，到redis消息队列
            sendSqlMessage(param, batchResultBase, sql, warningLevelDTO, driverSession, pageNumber, batchNumber);

            //根据计数器判断执行情况
            while (true) {
                try {
                    //休眠
                    Thread.sleep(3000);
                    if (!redisHelper.hasKey(HANDLE_EXCEPTION_BATCH_NUM + batchResultBase.getResultBaseId())) {
                        //消息处理完，键被删除了
                        break;
                    }
                    if (redisHelper.hasKey(HANDLE_EXCEPTION_BATCH_NUM + batchResultBase.getResultBaseId()) &&
                            redisHelper.strGet(HANDLE_EXCEPTION_BATCH_NUM + batchResultBase.getResultBaseId()).equals(redisHelper.strGet(TOTAL_EXCEPTION_BATCH_NUM + batchResultBase.getResultBaseId()))) {
                        //如果处理记录相等时，也执行结束
                        break;
                    }
                    //判断是否异常了
                    if (redisHelper.hasKey(ERROR_FLAG + batchResultBase.getResultBaseId())) {
                        //删除，并抛出异常
                        mongoTemplate.dropCollection(String.format("%d_%d", batchResultBase.getPlanBaseId(), batchResultBase.getResultBaseId()));
                        throw new CommonException("异常数据获取失败！");
                    }
                } catch (InterruptedException e) {
                    log.error("线程中断，设置异常标识");
                    e.printStackTrace();
                    //线程被中断跳出循环
                    redisHelper.strSet(ERROR_FLAG + batchResultBase.getResultBaseId(), "Y");
                    break;
                }
            }
            if (redisHelper.hasKey(ERROR_FLAG + batchResultBase.getResultBaseId())) {
                //删除，并抛出异常
                mongoTemplate.dropCollection(String.format("%d_%d", batchResultBase.getPlanBaseId(), batchResultBase.getResultBaseId()));
                throw new CommonException("异常数据获取失败！");
            }
            long end = System.currentTimeMillis();
            log.info("异常数据获取结束,耗时{}s", (end - start) / 1000);
        } else {
            //无需分页
            getExceptionResult(param, batchResultBase, sql, driverSession, warningLevelDTO);
            log.info("异常数据获取结束");
        }
    }

    private static void sendSqlMessage(MeasureParamDO param, BatchResultBase batchResultBase, String sql, WarningLevelDTO warningLevelDTO, DriverSession driverSession, int pageNumber, int batchNumber) {
        for (int i = 0; i < pageNumber; i++) {
            try {
                //如果当然质检项结果id存在异常标识，停止发送
                if (redisHelper.hasKey(ERROR_FLAG + batchResultBase.getResultBaseId())) {
                    break;
                }
                //获取分页sql
                String pageSql = driverSession.getPageSql(sql, PageRequest.of(i, batchNumber));
                MessageDTO messageDTO = MessageDTO.builder().param(param).batchResultBase(batchResultBase)
                        .sql(pageSql)
                        .warningLevelDTO(warningLevelDTO)
                        .build();
                redisHelper.lstLeftPush(messageKey, JsonUtil.toJson(messageDTO));
                //定义计数器，成功+1
                redisHelper.strIncrement(TOTAL_EXCEPTION_BATCH_NUM + batchResultBase.getResultBaseId(), 1L);
            } catch (Exception e) {
                // todo 如果有异常，则定义删除标识
                redisHelper.strSet(ERROR_FLAG + batchResultBase.getResultBaseId(), "Y");
                break;
            }
        }
    }

    public static void getExceptionResult(MeasureParamDO param, BatchResultBase batchResultBase, String sql, DriverSession driverSession, WarningLevelDTO warningLevelDTO) {
        List<Map<String, Object>> exceptionMapList = driverSession.executeOneQuery(param.getSchema(), sql);
        if (CollectionUtils.isNotEmpty(exceptionMapList)) {
            //如果是hive，则去除最后一个结果，最后一个结果是hive-sql执行日志，并非异常数据
            if (DbType.hive.equals(driverSession.getDbType())) {
                //判断最后一行是不是hive-sql的执行日志，如果是则移除，如果不是则不处理
                if (exceptionMapList.get(exceptionMapList.size() - 1).get("hive-sql") != null) {
                    exceptionMapList.remove(exceptionMapList.size() - 1);
                }
            }
            //值集转换map
            Map<String, String> warningLevelMap = new HashMap<>();
            Map<String, String> checkItemMap = new HashMap<>();
            Map<String, String> countTypeMap = new HashMap<>();
            //每一条异常数据存上规则名和异常信息
            exceptionMapList.forEach(map -> {
                List<String> values = map.values().stream().map(String::valueOf).collect(Collectors.toList());
                //将所有的数据
                map.put(PK, String.join("-", values));
                map.put(PLAN_BASE_ID, batchResultBase.getPlanBaseId());
                map.put(RESULT_BASE_ID, batchResultBase.getResultBaseId());
                map.put(RULE_NAME, param.getBatchResultRuleDTO().getRuleName());
                String warningLevelMeaning = warningLevelMap.get(warningLevelDTO.getWarningLevel());
                if (warningLevelMeaning == null) {
                    warningLevelMeaning = lovAdapter.queryLovMeaning(WARNING_LEVEL_LOV, BaseConstants.DEFAULT_TENANT_ID, warningLevelDTO.getWarningLevel());
                    warningLevelMap.put(warningLevelDTO.getWarningLevel(), warningLevelMeaning);
                }

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
                String checkItemMeaning = checkItemMap.get(param.getCheckItem());
                if (checkItemMeaning == null) {
                    checkItemMeaning = lovAdapter.queryLovMeaning(CHECK_ITEM_LOV, BaseConstants.DEFAULT_TENANT_ID, param.getCheckItem());
                    checkItemMap.put(param.getCheckItem(), checkItemMeaning);
                }

                String countTypeMeaning = countTypeMap.get(param.getCountType());
                if (countTypeMeaning == null) {
                    countTypeMeaning = lovAdapter.queryLovMeaning(COUNT_TYPE_LOV, BaseConstants.DEFAULT_TENANT_ID, param.getCountType());
                    countTypeMap.put(param.getCountType(), countTypeMeaning);
                }

                String exception = String.format("【%s】 %s 满足告警条件 %s",
                        param.getFieldName(),
                        String.format("%s %s", checkItemMeaning, countTypeMeaning),
                        Strings.isNotEmpty(warningCondition) ? String.format("【%s】", warningCondition) : "");
                map.put(EXCEPTION_INFO, String.format("%s(%s)", warningLevelMeaning, exception));
            });
        }
        //collection 使用质检项Id_结果Id
        mongoTemplate.insert(exceptionMapList, String.format("%d_%d", batchResultBase.getPlanBaseId(), batchResultBase.getResultBaseId()));
    }

    /**
     * 根据消息获取异常数据
     *
     * @param message
     */
    public static void getExceptionResult(String message) {
        BatchResultBase batchResultBase = null;
        try {
            MessageDTO messageDTO = JsonUtil.toObj(message, MessageDTO.class);
            MeasureParamDO param = messageDTO.getParam();
            WarningLevelDTO warningLevelDTO = messageDTO.getWarningLevelDTO();
            batchResultBase = messageDTO.getBatchResultBase();
            String sql = messageDTO.getSql();
            //如果当然质检项结果id存在异常标识，停止获取
            if (redisHelper.hasKey(ERROR_FLAG + batchResultBase.getResultBaseId())) {
                return;
            }
            DriverSession driverSession = driverSessionService.getDriverSession(batchResultBase.getTenantId(), param.getPluginDatasourceDTO().getDatasourceCode());
            getExceptionResult(param, batchResultBase, sql, driverSession, warningLevelDTO);
        } catch (Throwable throwable) {
            redisHelper.strSet(ERROR_FLAG + Objects.requireNonNull(batchResultBase).getResultBaseId(), "Y");
        } finally {
            //处理计数器加1
            redisHelper.strIncrement(HANDLE_EXCEPTION_BATCH_NUM + Objects.requireNonNull(batchResultBase).getResultBaseId(), 1L);
            //判断是否清空异常标识
            if (redisHelper.hasKey(HANDLE_EXCEPTION_BATCH_NUM + batchResultBase.getResultBaseId()) &&
                    redisHelper.strGet(HANDLE_EXCEPTION_BATCH_NUM + batchResultBase.getResultBaseId()).equals(redisHelper.strGet(TOTAL_EXCEPTION_BATCH_NUM + batchResultBase.getResultBaseId()))) {
                log.info("处理完成，后置清理");
                redisHelper.delKey(TOTAL_EXCEPTION_BATCH_NUM + batchResultBase.getResultBaseId());
                redisHelper.delKey(HANDLE_EXCEPTION_BATCH_NUM + batchResultBase.getResultBaseId());
                if (redisHelper.hasKey(ERROR_FLAG + batchResultBase.getResultBaseId())) {
                    //删除数据
                    mongoTemplate.dropCollection(String.format("%d_%d", batchResultBase.getPlanBaseId(), batchResultBase.getResultBaseId()));
                }
                redisHelper.delKey(ERROR_FLAG + batchResultBase.getResultBaseId());
            }

            //线程执行完，进行回收
            ExceptionDataConsumer exceptionDataConsumer = context.getBean(ExceptionDataConsumer.class);
            exceptionDataConsumer.recycleThread();
        }
    }

}
