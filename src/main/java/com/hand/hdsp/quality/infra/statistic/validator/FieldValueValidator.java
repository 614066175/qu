package com.hand.hdsp.quality.infra.statistic.validator;

import com.hand.hdsp.core.value.handler.ValueHandler;
import com.hand.hdsp.quality.api.dto.AimStatisticsDTO;
import com.hand.hdsp.quality.api.dto.DataFieldDTO;
import com.hand.hdsp.quality.api.dto.StandardAimDTO;
import com.hand.hdsp.quality.infra.measure.MeasureUtil;
import com.hand.hdsp.quality.infra.statistic.regular.RegularValidUtil;
import com.hand.hdsp.quality.infra.util.ApplicationContextUtil;
import io.choerodon.core.exception.CommonException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.util.Strings;
import org.hzero.boot.driver.app.service.DriverSessionService;
import org.hzero.boot.platform.lov.adapter.LovAdapter;
import org.hzero.boot.platform.lov.dto.LovValueDTO;
import org.hzero.starter.driver.core.infra.meta.Column;
import org.hzero.starter.driver.core.infra.util.UUIDUtils;
import org.hzero.starter.driver.core.session.DriverSession;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.hand.hdsp.quality.infra.constant.PlanConstant.StandardValueType.*;

/**
 * <p>
 * 字段值校验器，（数据格式，值域）
 * </p>
 *
 * @author lgl 2022/8/1 11:11
 * @since 1.0
 */
@Component
@Order(2)
@Slf4j
public class FieldValueValidator implements StatisticValidator {

    public static final String VALUE_VALIDA_SQL = "select count(*) from ${table} where ${filter} ";

    public static final String VALUE_SQL = "select ${field} from ${table} where ${filter} ";

    public static final String PARAM_HOLDER_PATTERN = ":{%s}";

    public final LovAdapter lovAdapter;


    private final DriverSessionService driverSessionService;

    public FieldValueValidator(LovAdapter lovAdapter, DriverSessionService driverSessionService) {
        this.lovAdapter = lovAdapter;
        this.driverSessionService = driverSessionService;
    }


    @Override
    public boolean valid(DataFieldDTO dataFieldDTO, StandardAimDTO standardAimDTO, AimStatisticsDTO aimStatisticsDTO) {
        //获取所有的数据进行数据值校验
        DriverSession driverSession = driverSessionService.getDriverSession(standardAimDTO.getTenantId(), standardAimDTO.getDatasourceCode());
        //获取字段
        String fieldName = standardAimDTO.getFieldName();
        if (fieldName.contains("(")) {
            fieldName = fieldName.substring(0, fieldName.indexOf("("));
        }
        //字段做格式化处理
        String formatFieldName = String.format(driverSession.getFieldFormat(), fieldName);

        List<String> conditions = new ArrayList<>();

        //不可为空
        if (dataFieldDTO.getNullFlag() == 0) {
            conditions.add(String.format(" %s is not null", formatFieldName));
        }

        Map<String, Object> paramMap = new HashMap<>();

        //值域条件
        if (StringUtils.isNotEmpty(dataFieldDTO.getValueType()) && StringUtils.isNotEmpty(dataFieldDTO.getValueRange())) {
            List<Column> columnList = driverSession.columnMetaData(standardAimDTO.getSchemaName(), standardAimDTO.getTableName());
            String filedName = standardAimDTO.getFieldName().split("\\(")[0];
            Optional<Column> optional = columnList.stream().filter(column -> column.getColumnName().equals(filedName)).findFirst();
            Column column;
            if (optional.isPresent()) {
                column = optional.get();

            } else {
                throw new CommonException("字段不存在");
            }

            String condition;
            String paramFormat = driverSession.getParamFormat(column.getTypeName());

            String dbType = driverSession.getDbType().name().toLowerCase();
            ValueHandler valueHandler = null;
            try {
                valueHandler = ApplicationContextUtil.findBean(String.format("%sValueHandler", dbType.toLowerCase()), ValueHandler.class);
            } catch (Exception e) {
                log.error("不存在对应数据源的值处理器");
            }
            if (valueHandler == null) {
                //使用通用的值处理器
                valueHandler = ApplicationContextUtil.findBean("commonValueHandler", ValueHandler.class);
            }
            ValueHandler finalValueHandler = valueHandler;
            List<String> paramHolderList = new ArrayList<>();

            switch (dataFieldDTO.getValueType()) {
                case AREA:
                    //范围
                    String[] split = dataFieldDTO.getValueRange().split(",");
                    if (split.length != 2) {
                        throw new CommonException("参数不正确");
                    }
                    if (StringUtils.isNotEmpty(split[0])) {
                        //占位符
                        String paramName = formatFieldName + "_" + UUIDUtils.generateShortUUID();
                        String paramHolder = String.format(PARAM_HOLDER_PATTERN, paramName);
                        paramHolder = String.format(paramFormat, paramHolder);
                        condition = String.format(" %s > = %s", formatFieldName, paramHolder);
                        conditions.add(condition);

                        paramMap.put(paramName, finalValueHandler.convertValueType(column.getTypeName(), split[0]));
                    }
                    if (StringUtils.isNotEmpty(split[1])) {
                        //占位符
                        String paramName = formatFieldName + "_" + UUIDUtils.generateShortUUID();
                        String paramHolder = String.format(PARAM_HOLDER_PATTERN, paramName);
                        paramHolder = String.format(paramFormat, paramHolder);
                        condition = String.format(" %s < = %s", formatFieldName, paramHolder);
                        conditions.add(condition);

                        paramMap.put(paramName, finalValueHandler.convertValueType(column.getTypeName(), split[1]));
                    }
                    break;
                case ENUM:
                    String[] values = dataFieldDTO.getValueRange().split(",");
                    for (String value : values) {
                        //占位符
                        String paramName = formatFieldName + "_" + UUIDUtils.generateShortUUID();
                        String paramHolder = String.format(PARAM_HOLDER_PATTERN, paramName);
                        paramHolder = String.format(paramFormat, paramHolder);
                        paramHolderList.add(paramHolder);
                        paramMap.put(paramName, finalValueHandler.convertValueType(column.getTypeName(), value));
                    }
                    condition = String.format("%s in (%s)",
                            formatFieldName,
                            Strings.join(paramHolderList, ','));
                    conditions.add(condition);
                    break;
                case VALUE_SET:
                    //查询值集结果
                    List<LovValueDTO> lovValueDTOS = lovAdapter.queryLovValue(dataFieldDTO.getValueRange(), dataFieldDTO.getTenantId());
                    if (CollectionUtils.isEmpty(lovValueDTOS)) {
                        throw new CommonException("值集【" + dataFieldDTO.getValueRange() + "】为空");
                    }
                    List<String> lovValues = lovValueDTOS.stream().map(LovValueDTO::getValue).collect(Collectors.toList());
                    for (String value : lovValues) {
                        //占位符
                        String paramName = formatFieldName + "_" + UUIDUtils.generateShortUUID();
                        String paramHolder = String.format(PARAM_HOLDER_PATTERN, paramName);
                        paramHolder = String.format(paramFormat, paramHolder);
                        paramHolderList.add(paramHolder);
                        paramMap.put(paramName, finalValueHandler.convertValueType(column.getTypeName(), value));
                    }
                    condition = String.format("%s in (%s)",
                            formatFieldName,
                            Strings.join(paramHolderList, ','));
                    conditions.add(condition);
                    break;
                case LOV_VIEW:
                    //查询值集视图值字段结果 todo
                    break;
                default:
                    throw new CommonException("not support value type");
            }
        }


        Map<String, String> variable = new HashMap();
        variable.put("table", standardAimDTO.getTableName());
        variable.put("field", formatFieldName);

        if (StringUtils.isNotEmpty(dataFieldDTO.getDataPattern())) {
            //正则
            String regularCondition = RegularValidUtil.getRegularCondition(driverSession.getPluginDatasourceVO().getDbType());
            if (StringUtils.isEmpty(regularCondition)) {
                //java形式
                String sql = MeasureUtil.replaceVariable(VALUE_SQL, variable, StringUtils.join(conditions, " and "));
                List<Map<String, Object>> result = driverSession.executeOneQuery(standardAimDTO.getSchemaName(), sql);

                //对结果进行遍历，去进行数据比对
                //取原始字段名
                String finalFieldName = fieldName;
                AtomicReference<Long> compliantRow = new AtomicReference<>(0L);
                Pattern pattern = Pattern.compile(dataFieldDTO.getDataPattern());
                result.forEach(map -> {
                    String value = map.get(finalFieldName) == null ? StringUtils.EMPTY : String.valueOf(map.get(finalFieldName));
                    //对值进行校验
                    if (StringUtils.isNotEmpty(dataFieldDTO.getDataPattern())) {
                        Matcher matcher = pattern.matcher(value);
                        if (!matcher.find()) {
                            //不匹配直接下一条
                            return;
                        }
                    }
                    compliantRow.getAndSet(compliantRow.get() + 1);
                });
                aimStatisticsDTO.setCompliantRow(compliantRow.get());
                return true;
            } else {
                //sql形式
                conditions.add(regularCondition);
                variable.put("regexp", dataFieldDTO.getDataPattern());
            }
        }

        //sql检验
        String sql = MeasureUtil.replaceVariable(VALUE_VALIDA_SQL, variable, StringUtils.join(conditions, " and "));
        List<Map<String, Object>> maps = driverSession.executeOneQuery(standardAimDTO.getSchemaName(), sql, paramMap);
        //设置合规行数
        aimStatisticsDTO.setCompliantRow(Long.parseLong(maps.get(0).values().toArray()[0].toString()));
        return true;
    }
}
