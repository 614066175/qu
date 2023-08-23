package org.xdsp.quality.infra.constant;

/**
 * <p>
 * 告警模板
 * </p>
 *
 * @author lgl 2020/09/17 20:59
 * @since 1.0
 */
public interface AlertTemplate {
    /**
     * 方案名称
     */
    String PLAN_NAME = "planName";
    /**
     * 评论
     */
    String MARK = "mark";
    /**
     * 开始时间
     */
    String START_DATE = "startDate";
    /**
     * 状态
     */
    String STATUS = "status";
    /**
     * 异常信息
     */
    String EXCEPTION_INFO = "exceptionInfo";
    /**
     * 告警等级
     */
    String WARNING_LEVEL = "warningLevel";
}
