package com.hand.hdsp.quality.infra.util;

import com.hand.hdsp.quality.infra.constant.ErrorCode;
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
            case "INT4":
            case "SERIAL":
            case "SMALLINT":
            case "TINYINT":
            case "INT UNSIGNED":
            case "BOOL":
                return "INTEGER";
            case "DECIMAL":
            case "NUMERIC":
            case "NUMBER":
            case "FLOAT":
                return "DECIMAL";
            case "VARCHAR":
            case "VARCHAR2":
            case "STRING":
            case "TEXT":
                return "STRING";
            case "BIGINT":
                return "BIGINTEGER";
            case "DATETIME":
            case "TIMESTAMP":
                return "DATETIME";
            default:
                throw new CommonException(ErrorCode.UNKNOWN_DATATYPE);
        }
    }
}
