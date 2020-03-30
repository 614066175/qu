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
     * 方案告警等级-对应业务类型-表级
     */
    String WARNING_LEVEL_TABLE = "xqua_batch_plan_table_line";

    /**
     * 方案告警等级-对应业务类型-字段级
     */
    String WARNING_LEVEL_FIELD = "xqua_batch_plan_field_line";

    /**
     * 方案告警等级-对应业务类型-表间
     */
    String WARNING_LEVEL_REL_TABLE = "xqua_batch_plan_rel_table_line";

    /**
     * 比较方式
     */
    public interface CompareWay {
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
    public interface PlanStatus {
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
}
