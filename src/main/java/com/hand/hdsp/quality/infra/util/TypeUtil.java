package com.hand.hdsp.quality.infra.util;

import io.choerodon.core.exception.CommonException;

/**
 * <p>
 * 类型转换工具类
 * </p>
 *
 * @author lgl 2022/7/5 11:42
 * @since 1.0
 */
public class TypeUtil {
    private TypeUtil() {
        throw new IllegalStateException("util class!");
    }

    public static String convertType(String type) {
        switch (type.toUpperCase()) {
            case "INT":
            case "TINYINT":
            case "BIGINT":
                return "INTEGER";
            case "DECIMAL":
                return "DECIMAL";
            default:
                throw new CommonException("未找到数据类型");
        }
    }
}
