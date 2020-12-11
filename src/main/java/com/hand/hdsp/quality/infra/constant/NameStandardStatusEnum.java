package com.hand.hdsp.quality.infra.constant;

/**
 * <p>
 * MysqlDriverSession
 * </p>
 *
 * @author 张鹏 2020/12/11 9:48
 * @since 1.0.0
 */
public enum NameStandardStatusEnum {
    /**
     * 就绪状态
     */
    READY("READY","就绪"),

    /**
     * 运行中
     */
    RUNNING("RUNNING","运行中"),

    /**
     * 执行失败
     */
    FAILED("FAILED","执行失败"),

    /**
     * 执行成功
     */
    SUCCESS("SUCCESS","执行成功");
    private final String statusCode;

    private final String statusMeaning;

    public String getStatusCode() {
        return statusCode;
    }

    public String getStatusMeaning() {
        return statusMeaning;
    }

    NameStandardStatusEnum(String statusCode, String statusMeaning) {
        this.statusCode = statusCode;
        this.statusMeaning = statusMeaning;
    }
}
