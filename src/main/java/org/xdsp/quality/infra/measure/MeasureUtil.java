package org.xdsp.quality.infra.measure;

import io.choerodon.core.convertor.ApplicationContextHelper;
import io.choerodon.core.exception.CommonException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.hzero.core.base.BaseConstants;
import org.springframework.util.Assert;
import org.xdsp.quality.api.dto.WarningLevelDTO;
import org.xdsp.quality.domain.entity.BatchResultItem;
import org.xdsp.quality.domain.entity.ItemTemplateSql;
import org.xdsp.quality.domain.repository.ItemTemplateSqlRepository;
import org.xdsp.quality.infra.constant.ErrorCode;
import org.xdsp.quality.infra.constant.PlanConstant;
import org.xdsp.quality.infra.util.JsonUtils;
import org.xdsp.quality.infra.vo.WarningLevelVO;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.ParseException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * <p>评估工具类</p>
 *
 * @author feng.liu01@hand-china.com 2020-03-24 16:19:53
 */
@Slf4j
public class MeasureUtil {

    private MeasureUtil() {
    }

    private static final Pattern PLACEHOLDER_PATTERN = Pattern.compile(".*\\$\\{(.*)}.*");
    private static final String FILTER_PLACEHOLDER = "${filter}";
    private static final String FIXED_VALUE_WARNING_INFO = "固定值满足告警条件";
    private static final String FIXED_RANGE_WARNING_INFO = "固定值在告警范围内";
    private static final String VOLATILITY_WARNING_INFO = "波动率在告警范围内";
    private static final String EMPTY_SQL=" (%s is null or %s ='') ";


    /**
     * 固定值比较工具
     *
     * @param compareWay       比较方式
     * @param value            实际值
     * @param warningLevelList 告警等级
     * @param batchResultItem  结果
     */
    public static void fixedCompare(String compareWay,
                                    String value,
                                    List<WarningLevelDTO> warningLevelList,
                                    BatchResultItem batchResultItem) {

        if (PlanConstant.CompareWay.VALUE.equals(compareWay)) {
            fixedCompareValue(value, warningLevelList, batchResultItem);
        } else if (PlanConstant.CompareWay.RANGE.equals(compareWay)) {
            fixedCompareRange(value, warningLevelList, batchResultItem);
        }
    }


    /**
     * 波动率计算公式
     * 波动率 =（样本-基准值）/基准值
     *
     * @param sample 样本
     * @param base   基准值
     * @return 波动率
     */
    public static BigDecimal volatility(BigDecimal sample, BigDecimal base) {
        if (BigDecimal.ZERO.compareTo(base) == 0) {
            return null;
        }
        return sample.subtract(base).divide(base, 2, RoundingMode.HALF_UP).abs().multiply(new BigDecimal(100));
    }

    /**
     * 波动率比较工具
     *
     * @param compareWay       比较方式
     * @param sample           样本
     * @param base             基准值
     * @param warningLevelList 告警等级
     * @param batchResultItem  结果
     */
    public static void volatilityCompare(String compareWay,
                                         BigDecimal sample,
                                         BigDecimal base,
                                         List<WarningLevelDTO> warningLevelList,
                                         BatchResultItem batchResultItem) {
        switch (compareWay) {
            case PlanConstant.CompareWay.ABSOLUTE_VALUE:
                volatilityCompareCount(sample, base, warningLevelList, batchResultItem);
                break;
            case PlanConstant.CompareWay.RISE_RATE:
                //只有上升时才计算波动率
                if (sample.compareTo(base) > 0) {
                    volatilityCompareCount(sample, base, warningLevelList, batchResultItem);
                } else {
                    batchResultItem.setActualValue("0%");
                }
                break;
            case PlanConstant.CompareWay.DOWN_RATE:
                //只有下降时才计算波动率
                if (sample.compareTo(base) < 0) {
                    volatilityCompareCount(sample, base, warningLevelList, batchResultItem);
                } else {
                    batchResultItem.setActualValue("0%");
                }
                break;
            default:
                break;

        }
    }

    /**
     * 基础值/总行数
     *
     * @param baseValue 基础值
     * @param dataCount 总行数
     * @return
     */
    public static long divide(long baseValue, long dataCount) {
        return BigDecimal.valueOf(baseValue).divide(BigDecimal.valueOf(dataCount), 2, RoundingMode.HALF_UP).longValue();
    }

    /**
     * 替换变量
     *
     * @param template
     * @param variables
     * @param filter
     * @return
     */
    public static String replaceVariable(String template, Map<String, String> variables, String filter) {
        String sqlAction = template;
        if (StringUtils.isBlank(filter)) {
            filter = "1 = 1";
        }
        sqlAction = sqlAction.replace(FILTER_PLACEHOLDER, filter);
        log.info("Succeed to replace {} into {}", FILTER_PLACEHOLDER, filter);
        for (Map.Entry<String, String> entry : variables.entrySet()) {
            String name = entry.getKey();
            String value = entry.getValue();
            String placeHolder = "\\$\\{" + name + "}";
            if (StringUtils.isNotBlank(value)) {
                value = Matcher.quoteReplacement(value);
            }
            sqlAction = sqlAction.replaceAll(placeHolder, value);
            log.info("Succeed to replace {} into {}", placeHolder, value);
        }

        if (PLACEHOLDER_PATTERN.matcher(sqlAction).matches()) {
            throw new CommonException(ErrorCode.CONVERT_SQL);
        }

        log.info("评估sql：" + sqlAction);
        return sqlAction;
    }

    /**
     * 处理字段：去掉类型
     *
     * @param fieldName
     * @return
     */
    public static String handleFieldName(String fieldName) {
        if (StringUtils.isBlank(fieldName)) {
            return null;
        }
        String[] strings = fieldName.split(BaseConstants.Symbol.COMMA);
        List<String> list = new ArrayList<>();
        for (String column : strings) {
            if (StringUtils.isNotEmpty(column) && column.contains("(")) {
                list.add(column.substring(0, column.indexOf('(')));
            } else {
                list.add(column);
            }
        }
        return StringUtils.join(list, BaseConstants.Symbol.COMMA);
    }

    /**
     * 处理空值校验
     *
     *
     * @param fieldName
     * @param datasourceType
     * @return
     */
    public static String handleEmpty(String fieldName, String datasourceType) {
        if (StringUtils.isBlank(fieldName)) {
            return null;
        }
        String[] strings = fieldName.split(BaseConstants.Symbol.COMMA);
        List<String> list = new ArrayList<>();
        for (String column : strings) {
            if (StringUtils.isNotEmpty(column) && column.contains("(")) {
                list.add(column.substring(0, column.indexOf('(')));
            } else {
                list.add(column);
            }
        }
        // ( a is null or a = '') and (b is null or b ='')
        ItemTemplateSqlRepository templateSqlRepository = ApplicationContextHelper.getContext().getBean(ItemTemplateSqlRepository.class);
        ItemTemplateSql itemTemplateSql = templateSqlRepository.selectSql(ItemTemplateSql.builder()
                .checkItem("FIELD_EMPTY_CONDITION")
                .datasourceType(datasourceType)
                .build());
        String sqlContent = itemTemplateSql.getSqlContent();
        List<String> emptySqlList = list.stream()
                .map(column -> String.format(sqlContent, column, column))
                .collect(Collectors.toList());
        return StringUtils.join(emptySqlList, "and");
    }

    /**
     * 日期转换，如果失败则返回 null
     *
     * @param s
     * @return
     */
    private static Date parseDate(String s) {
        try {
            //如果转换成功，则按日期处理
            return DateUtils.parseDate(s,
                    BaseConstants.Pattern.DATE,
                    BaseConstants.Pattern.DATETIME,
                    BaseConstants.Pattern.DATETIME_SSS,
                    BaseConstants.Pattern.DATETIME_MM,
                    BaseConstants.Pattern.TIME_SS);

        } catch (ParseException e) {
            return null;
        }
    }

    /**
     * 固定值比较工具-值比较
     *
     * @param value            实际值
     * @param warningLevelList 告警等级
     * @param batchResultItem  结果
     */
    private static void fixedCompareValue(String value,
                                          List<WarningLevelDTO> warningLevelList,
                                          BatchResultItem batchResultItem) {
        for (WarningLevelDTO warningLevelDTO : warningLevelList) {
            if (PlanConstant.CompareSymbol.EQUAL.equals(warningLevelDTO.getCompareSymbol())) {
                // 如果是数字，则用 BigDecimal.compareTo 比较
                if (NumberUtils.isParsable(value) && NumberUtils.isParsable(warningLevelDTO.getExpectedValue())) {
                    if (new BigDecimal(value).compareTo(new BigDecimal(warningLevelDTO.getExpectedValue())) == 0) {
                        batchResultItem.setWarningLevel(
                                JsonUtils.object2Json(
                                        Collections.singletonList(WarningLevelVO.builder()
                                                .warningLevel(warningLevelDTO.getWarningLevel())
                                                .build()))
                        );
                        batchResultItem.setExceptionInfo(FIXED_VALUE_WARNING_INFO);
                        warningLevelDTO.setIfAlert(1L);
                        break;
                    } else {
                        //如果都是数字，就用数字比较
                        continue;
                    }
                }

                //日期
                if (parseDate(value) != null && parseDate(warningLevelDTO.getExpectedValue()) != null) {
                    Date valueDate = parseDate(value);
                    if (valueDate != null) {
                        Date expectedDate = parseDate(warningLevelDTO.getExpectedValue());
                        Assert.notNull(expectedDate, ErrorCode.EXPECTED_VALUE_IS_NOT_DATE);
                        if (DateUtils.isSameInstant(valueDate, expectedDate)) {
                            batchResultItem.setWarningLevel(
                                    JsonUtils.object2Json(
                                            Collections.singletonList(WarningLevelVO.builder()
                                                    .warningLevel(warningLevelDTO.getWarningLevel())
                                                    .build()))
                            );
                            batchResultItem.setExceptionInfo(FIXED_VALUE_WARNING_INFO);
                            warningLevelDTO.setIfAlert(1L);
                            break;
                        }
                    } else {
                        continue;
                    }
                }


                // 字符串
                if (warningLevelDTO.getExpectedValue().equals(value)) {
                    batchResultItem.setWarningLevel(
                            JsonUtils.object2Json(
                                    Collections.singletonList(WarningLevelVO.builder()
                                            .warningLevel(warningLevelDTO.getWarningLevel())
                                            .build()))
                    );
                    warningLevelDTO.setIfAlert(1L);
                    batchResultItem.setExceptionInfo(FIXED_VALUE_WARNING_INFO);
                    break;
                }

            } else if (PlanConstant.CompareSymbol.NOT_EQUAL.equals(warningLevelDTO.getCompareSymbol())) {
                // 如果是数字，则用 BigDecimal.compareTo 比较
                if (NumberUtils.isParsable(value)
                        && NumberUtils.isParsable(warningLevelDTO.getExpectedValue())) {
                    if (new BigDecimal(value).compareTo(new BigDecimal(warningLevelDTO.getExpectedValue())) != 0) {
                        batchResultItem.setWarningLevel(
                                JsonUtils.object2Json(
                                        Collections.singletonList(WarningLevelVO.builder()
                                                .warningLevel(warningLevelDTO.getWarningLevel())
                                                .build()))
                        );
                        batchResultItem.setExceptionInfo(FIXED_VALUE_WARNING_INFO);
                        warningLevelDTO.setIfAlert(1L);
                        break;
                    } else {
                        //没问题，这直接下个告警等级
                        continue;
                    }

                }

                //日期
                if (parseDate(value) != null && parseDate(warningLevelDTO.getExpectedValue()) != null) {
                    Date valueDate = parseDate(value);
                    if (valueDate != null) {
                        Date expectedDate = parseDate(warningLevelDTO.getExpectedValue());
                        Assert.notNull(expectedDate, ErrorCode.EXPECTED_VALUE_IS_NOT_DATE);
                        if (!DateUtils.isSameInstant(valueDate, expectedDate)) {
                            batchResultItem.setWarningLevel(
                                    JsonUtils.object2Json(
                                            Collections.singletonList(WarningLevelVO.builder()
                                                    .warningLevel(warningLevelDTO.getWarningLevel())
                                                    .build()))
                            );
                            batchResultItem.setExceptionInfo(FIXED_VALUE_WARNING_INFO);
                            warningLevelDTO.setIfAlert(1L);
                            break;
                        } else {
                            continue;
                        }
                    }
                }

                // 字符串
                if (!warningLevelDTO.getExpectedValue().equals(value)) {
                    batchResultItem.setWarningLevel(
                            JsonUtils.object2Json(
                                    Collections.singletonList(WarningLevelVO.builder()
                                            .warningLevel(warningLevelDTO.getWarningLevel())
                                            .build()))
                    );
                    batchResultItem.setExceptionInfo(FIXED_VALUE_WARNING_INFO);
                    warningLevelDTO.setIfAlert(1L);
                    break;
                }
            }
        }
    }

    /**
     * 固定值比较工具-范围比较
     *
     * @param value            实际值
     * @param warningLevelList 告警等级
     * @param batchResultItem  结果
     */
    private static void fixedCompareRange(String value,
                                          List<WarningLevelDTO> warningLevelList,
                                          BatchResultItem batchResultItem) {
        if (NumberUtils.isParsable(value)) {
            BigDecimal actualValue = new BigDecimal(value);
            for (WarningLevelDTO warningLevel : warningLevelList) {
                String startValue = warningLevel.getStartValue();
                String endValue = warningLevel.getEndValue();
                if (StringUtils.isBlank(startValue) && StringUtils.isBlank(endValue)) {
                    throw new CommonException(ErrorCode.WARNING_LEVEL_RANGE_NOT_ALL_EMPTY);
                }
                boolean startResult = true;
                boolean endResult = true;
                if (StringUtils.isNotBlank(startValue)) {
                    startResult = new BigDecimal(startValue).compareTo(actualValue) <= 0;
                }
                if (StringUtils.isNotBlank(endValue)) {
                    endResult = new BigDecimal(endValue).compareTo(actualValue) >= 0;
                }
                if (startResult && endResult) {
                    batchResultItem.setWarningLevel(JsonUtils.object2Json(
                            Collections.singletonList(WarningLevelVO.builder()
                                    .warningLevel(warningLevel.getWarningLevel())
                                    .build())));
                    batchResultItem.setExceptionInfo(FIXED_RANGE_WARNING_INFO);
                    warningLevel.setIfAlert(1L);
                    break;
                }
            }
        }
        //日期
        Date valueDate = parseDate(value);
        if (valueDate != null) {
            for (WarningLevelDTO warningLevel : warningLevelList) {
                String startValue = warningLevel.getStartValue();
                String endValue = warningLevel.getEndValue();
                if (StringUtils.isBlank(startValue) && StringUtils.isBlank(endValue)) {
                    throw new CommonException(ErrorCode.WARNING_LEVEL_RANGE_NOT_ALL_EMPTY);
                }
                boolean startResult = true;
                boolean endResult = true;
                if (StringUtils.isNotBlank(startValue)) {
                    Date startDate = parseDate(startValue);
                    Assert.notNull(startDate, ErrorCode.EXPECTED_VALUE_IS_NOT_DATE);
                    startResult = startDate.compareTo(valueDate) <= 0;
                }
                if (StringUtils.isNotBlank(endValue)) {
                    Date endDate = parseDate(endValue);
                    Assert.notNull(endDate, ErrorCode.EXPECTED_VALUE_IS_NOT_DATE);

                    endResult = endDate.compareTo(valueDate) >= 0;
                }

                if (startResult && endResult) {
                    batchResultItem.setWarningLevel(JsonUtils.object2Json(
                            Collections.singletonList(WarningLevelVO.builder()
                                    .warningLevel(warningLevel.getWarningLevel())
                                    .build())));
                    batchResultItem.setExceptionInfo(FIXED_RANGE_WARNING_INFO);
                    warningLevel.setIfAlert(1L);
                    break;
                }
            }
        }
    }

    /**
     * 波动率比较工具-计算方法
     *
     * @param sample           样本
     * @param base             基准值
     * @param warningLevelList 告警等级
     * @param batchResultItem  结果
     */
    private static void volatilityCompareCount(BigDecimal sample,
                                               BigDecimal base,
                                               List<WarningLevelDTO> warningLevelList,
                                               BatchResultItem batchResultItem) {
        BigDecimal actualValue = volatility(sample, base);
        if (actualValue != null) {
            batchResultItem.setActualValue(actualValue.toString() + BaseConstants.Symbol.PERCENTAGE);
            for (WarningLevelDTO warningLevel : warningLevelList) {
                String startValue = warningLevel.getStartValue();
                String endValue = warningLevel.getEndValue();
                if (StringUtils.isBlank(startValue) && StringUtils.isBlank(endValue)) {
                    throw new CommonException(ErrorCode.WARNING_LEVEL_RANGE_NOT_ALL_EMPTY);
                }
                boolean startResult = true;
                boolean endResult = true;
                if (StringUtils.isNotBlank(startValue)) {
                    startResult = new BigDecimal(startValue).compareTo(actualValue) <= 0;
                }
                if (StringUtils.isNotBlank(endValue)) {
                    endResult = new BigDecimal(endValue).compareTo(actualValue) >= 0;
                }
                if (startResult && endResult) {
                    batchResultItem.setWarningLevel(JsonUtils.object2Json(
                            Collections.singletonList(WarningLevelVO.builder()
                                    .warningLevel(warningLevel.getWarningLevel())
                                    .build())));
                    batchResultItem.setExceptionInfo(VOLATILITY_WARNING_INFO);
                }
            }
        } else {
            batchResultItem.setActualValue("0%");
        }
    }
}
