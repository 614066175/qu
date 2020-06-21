package com.hand.hdsp.quality.infra.measure;

import com.hand.hdsp.quality.api.dto.WarningLevelDTO;
import com.hand.hdsp.quality.domain.entity.BatchResultItem;
import com.hand.hdsp.quality.infra.constant.PlanConstant;
import io.choerodon.core.exception.CommonException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.hzero.core.base.BaseConstants;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

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


    /**
     * 固定值比较工具类
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
            for (WarningLevelDTO warningLevelDTO : warningLevelList) {
                if (PlanConstant.CompareSymbol.EQUAL.equals(warningLevelDTO.getCompareSymbol())) {
                    // 字符串
                    if (warningLevelDTO.getExpectedValue().equals(value)) {
                        batchResultItem.setWarningLevel(warningLevelDTO.getWarningLevel());
                        batchResultItem.setExceptionInfo("达到阈值");
                        break;
                    }
                    // 如果是数字，则用 BigDecimal.compareTo 比较
                    if (NumberUtils.isParsable(value)
                            && new BigDecimal(value).compareTo(new BigDecimal(warningLevelDTO.getExpectedValue())) == 0) {
                        batchResultItem.setWarningLevel(warningLevelDTO.getWarningLevel());
                        batchResultItem.setExceptionInfo("达到阈值");
                        break;
                    }
                }
            }
        } else if (PlanConstant.CompareWay.RANGE.equals(compareWay)) {
            BigDecimal actualValue = new BigDecimal(value);
            for (WarningLevelDTO warningLevel : warningLevelList) {
                if (warningLevel.getStartValue().compareTo(actualValue) <= 0
                        && warningLevel.getEndValue().compareTo(actualValue) >= 0) {
                    batchResultItem.setWarningLevel(warningLevel.getWarningLevel());
                    batchResultItem.setExceptionInfo("波动率超过阈值范围");
                }
            }
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
     * 波动率比较工具类
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
                BigDecimal actualValue = volatility(sample, base);
                if (actualValue != null) {

                    batchResultItem.setWaveRate(actualValue.toString() + BaseConstants.Symbol.PERCENTAGE);
                    for (WarningLevelDTO planWarningLevel : warningLevelList) {
                        if (planWarningLevel.getStartValue().compareTo(actualValue) <= 0
                                && planWarningLevel.getEndValue().compareTo(actualValue) >= 0) {
                            batchResultItem.setWarningLevel(planWarningLevel.getWarningLevel());
                            batchResultItem.setExceptionInfo("波动率超过阈值范围");
                        }
                    }
                } else {
                    batchResultItem.setWaveRate("0%");
                }
                break;
            case PlanConstant.CompareWay.RISE_RATE:
                BigDecimal actualValue1 = volatility(sample, base);
                if (sample.compareTo(base) > 0 && actualValue1 != null) {
                    batchResultItem.setWaveRate(actualValue1.toString() + BaseConstants.Symbol.PERCENTAGE);
                    for (WarningLevelDTO planWarningLevel : warningLevelList) {
                        if (planWarningLevel.getStartValue().compareTo(actualValue1) <= 0
                                && planWarningLevel.getEndValue().compareTo(actualValue1) >= 0) {
                            batchResultItem.setWarningLevel(planWarningLevel.getWarningLevel());
                            batchResultItem.setExceptionInfo("波动率超过阈值范围");
                        }
                    }
                } else {
                    batchResultItem.setWaveRate("0%");
                }
                break;
            case PlanConstant.CompareWay.DOWN_RATE:
                BigDecimal actualValue2 = volatility(sample, base);
                if (sample.compareTo(base) < 0 && actualValue2 != null) {
                    batchResultItem.setWaveRate(actualValue2.toString() + BaseConstants.Symbol.PERCENTAGE);
                    for (WarningLevelDTO planWarningLevel : warningLevelList) {
                        if (planWarningLevel.getStartValue().compareTo(actualValue2) <= 0
                                && planWarningLevel.getEndValue().compareTo(actualValue2) >= 0) {
                            batchResultItem.setWarningLevel(planWarningLevel.getWarningLevel());
                            batchResultItem.setExceptionInfo("波动率超过阈值范围");
                        }
                    }
                } else {
                    batchResultItem.setWaveRate("0%");
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
            sqlAction = sqlAction.replaceAll(placeHolder, value);
            log.info("Succeed to replace {} into {}", placeHolder, value);
        }

        if (PLACEHOLDER_PATTERN.matcher(sqlAction).matches()) {
            throw new CommonException("Unable to convert SQL, replacing placeholders failed");
        }

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
        for (String string : strings) {
            list.add(string.substring(0, string.indexOf('(')));
        }
        return StringUtils.join(list, BaseConstants.Symbol.COMMA);
    }

}
