package com.hand.hdsp.quality.infra.statistic.regular;

import lombok.extern.slf4j.Slf4j;

/**
 * @Title: RegularValidUtil
 * @Description:
 * @author: lgl
 * @date: 2023/3/7 23:53
 */
@Slf4j
public class RegularValidUtil {

    public static String getRegularCondition(String dbType){
        switch (dbType.toLowerCase()){
            case "mysql":
                return " ${field} REGEXP '${regexp}'";
            case "oracle":
                return  "REGEXP_LIKE (${field},'${regexp}')";
            case "hive2":
                return "cast(${field} as String) REGEXP '${regexp}'";
            default:
                log.info("数据源类型{}-未适配sql形式",dbType);
        }
        return "";
    }
}
