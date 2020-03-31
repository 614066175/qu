package com.hand.hdsp.quality.infra.measure;

import com.hand.hdsp.quality.api.dto.BatchResultRuleDTO;
import com.hand.hdsp.quality.domain.entity.PlanWarningLevel;
import com.hand.hdsp.quality.infra.constant.PlanConstant;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

/**
 * <p>评估工具类</p>
 *
 * @author feng.liu01@hand-china.com 2020-03-24 16:19:53
 */
public class MeasureUtil {

    private MeasureUtil() {
    }

    /**
     * 固定值比较工具类
     *
     * @param compareWay         比较方式
     * @param actualValue        实际值
     * @param expectedValue      期望值
     * @param warningLevelList   告警等级
     * @param batchResultRuleDTO 结果
     */
    public static void fixedCompare(String compareWay,
                                    long actualValue,
                                    long expectedValue,
                                    List<PlanWarningLevel> warningLevelList,
                                    BatchResultRuleDTO batchResultRuleDTO) {
        boolean result = false;
        switch (compareWay) {
            case PlanConstant.CompareWay.LESS:
                if (actualValue < expectedValue) {
                    result = true;
                }
                break;
            case PlanConstant.CompareWay.GREATER:
                if (actualValue > expectedValue) {
                    result = true;
                }
                break;
            case PlanConstant.CompareWay.EQUAL:
                if (actualValue == expectedValue) {
                    result = true;
                }
                break;
            case PlanConstant.CompareWay.NOT_EQUAL:
                if (actualValue != expectedValue) {
                    result = true;
                }
                break;
            case PlanConstant.CompareWay.LESS_EQUAL:
                if (actualValue <= expectedValue) {
                    result = true;
                }
                break;
            case PlanConstant.CompareWay.GREATER_EQUAL:
                if (actualValue >= expectedValue) {
                    result = true;
                }
                break;
            default:
                break;

        }
        if (!result) {
            batchResultRuleDTO.setWarningLevel(warningLevelList.get(0).getWarningLevel());
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
        return sample.subtract(base).divide(base, 2, RoundingMode.HALF_UP).abs();
    }

    /**
     * 波动率比较工具类
     *
     * @param compareWay    比较方式
     * @param actualValue   实际值
     * @param expectedValue 期望值
     * @return 结果
     */
    /**
     * 波动率比较工具类
     *
     * @param compareWay         比较方式
     * @param sample             样本
     * @param base               基准值
     * @param warningLevelList   告警等级
     * @param batchResultRuleDTO 结果
     */
    public static void volatilityCompare(String compareWay,
                                         BigDecimal sample,
                                         BigDecimal base,
                                         List<PlanWarningLevel> warningLevelList,
                                         BatchResultRuleDTO batchResultRuleDTO) {
        switch (compareWay) {
            case PlanConstant.CompareWay.ABSOLUTE_VALUE:
                BigDecimal actualValue = volatility(sample, base);
                batchResultRuleDTO.setActualValue(actualValue.toString());
                for (PlanWarningLevel planWarningLevel : warningLevelList) {
                    if (planWarningLevel.getStartValue().compareTo(actualValue) <= 0
                            && planWarningLevel.getEndValue().compareTo(actualValue) >= 0) {
                        batchResultRuleDTO.setWarningLevel(planWarningLevel.getWarningLevel());
                    }
                }
                break;
            case PlanConstant.CompareWay.RISE_RATE:
                if (sample.compareTo(base) > 0) {
                    BigDecimal actualValue1 = volatility(sample, base);
                    batchResultRuleDTO.setActualValue(actualValue1.toString());
                    for (PlanWarningLevel planWarningLevel : warningLevelList) {
                        if (planWarningLevel.getStartValue().compareTo(actualValue1) <= 0
                                && planWarningLevel.getEndValue().compareTo(actualValue1) >= 0) {
                            batchResultRuleDTO.setWarningLevel(planWarningLevel.getWarningLevel());
                        }
                    }
                } else {
                    batchResultRuleDTO.setActualValue("0");
                }
                break;
            case PlanConstant.CompareWay.DOWN_RATE:
                if (sample.compareTo(base) < 0) {
                    BigDecimal actualValue1 = volatility(sample, base);
                    batchResultRuleDTO.setActualValue(actualValue1.toString());
                    for (PlanWarningLevel planWarningLevel : warningLevelList) {
                        if (planWarningLevel.getStartValue().compareTo(actualValue1) <= 0
                                && planWarningLevel.getEndValue().compareTo(actualValue1) >= 0) {
                            batchResultRuleDTO.setWarningLevel(planWarningLevel.getWarningLevel());
                        }
                    }
                } else {
                    batchResultRuleDTO.setActualValue("0");
                }
                break;
            default:
                break;

        }
    }

}
