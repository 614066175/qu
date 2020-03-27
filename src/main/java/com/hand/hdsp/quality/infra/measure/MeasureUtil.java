package com.hand.hdsp.quality.infra.measure;

import com.hand.hdsp.quality.infra.constant.PlanConstant;

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
     * @param compareWay    比较方式
     * @param actualValue   实际值
     * @param expectedValue 期望值
     * @return 结果
     */
    public static boolean compary(String compareWay, long actualValue, long expectedValue) {
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
        return result;
    }
}
