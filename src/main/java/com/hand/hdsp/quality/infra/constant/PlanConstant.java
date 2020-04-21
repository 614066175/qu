package com.hand.hdsp.quality.infra.constant;

/**
 * <p>方案常量类</p>
 *
 * @author feng.liu01@hand-china.com 2020-03-24 16:19:53
 */
public interface PlanConstant {

    /**
     * 数据质量任务类型
     */
    String JOB_TYPE = "QUALITY";

    /**
     * 数据质量任务级别
     */
    String JOB_LEVEL = "AUTO";

    /**
     * 默认主题ID
     */
    Long DEFAULT_THEME_ID = -7L;

    /**
     * 默认层次ID
     */
    Long DEFAULT_LAYER_ID = -7L;

    /**
     * 告警等级LOV
     */
    String LOV_WARNING_LEVEL = "HDSP.XQUA.WARNING_LEVEL";

    /**
     * 方案状态LOV
     */
    String LOV_PLAN_STATUS = "HDSP.XQUA.PLAN_STATUS";

    /**
     * 告警等级-正常
     */
    String WARNING_LEVEL_NORMAL = "NORMAL";

    /**
     * 比较方式
     */
    interface CompareWay {
        /**
         * 绝对值
         */
        String ABSOLUTE_VALUE = "ABSOLUTE_VALUE";
        /**
         * 上升
         */
        String RISE_RATE = "RISE_RATE";
        /**
         * 下降
         */
        String DOWN_RATE = "DOWN_RATE";
        /**
         * 小于
         */
        String LESS = "LESS";
        /**
         * 大于
         */
        String GREATER = "GREATER";
        /**
         * 等于
         */
        String EQUAL = "EQUAL";
        /**
         * 不等于
         */
        String NOT_EQUAL = "NOT_EQUAL";
        /**
         * 大于等于
         */
        String GREATER_EQUAL = "GREATER_EQUAL";
        /**
         * 小于等于
         */
        String LESS_EQUAL = "LESS_EQUAL";
    }

    /**
     * 方案状态
     */
    interface PlanStatus {
        /**
         * 未评估
         */
        String NOTASSESS = "NOTASSESS";
        /**
         * 运行中
         */
        String RUNNING = "RUNNING";
        /**
         * 执行成功
         */
        String SUCCESS = "SUCCESS";
        /**
         * 执行失败
         */
        String FAILED = "FAILED";

    }

    /**
     * 数据质量规则表规则类型
     */
    interface RuleType {
        /**
         * 表间关系
         */
        String TABLE_RELATION = "TABLE_RELATION";

    }


    /**
     * 批数据方案结果表-规则类型
     */
    interface ResultRuleType {
        String TABLE = "TABLE";
        String FIELD = "FIELD";
        String REL_TABLE = "REL_TABLE";
    }


    /**
     * 规则校验方式 HDSP.XQUA.CHECK_WAY
     */
    interface CheckWay {
        /**
         * 常规校验
         */
        String COMMON = "COMMON";
        /**
         * 正则表达式
         */
        String REGULAR = "REGULAR";
    }
}
