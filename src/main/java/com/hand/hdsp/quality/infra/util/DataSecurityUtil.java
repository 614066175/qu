package com.hand.hdsp.quality.infra.util;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.hzero.mybatis.helper.DataSecurityHelper;

/**
 * @author dell
 */
@UtilityClass
@Slf4j
public class DataSecurityUtil {

    /**
     * 解密参数
     *
     * @param param 加密参数
     * @return 解密结果
     */
    public static String decrypt(String param) {
        try {
            return DataSecurityHelper.decrypt(param);
        } catch (Exception e) {
            log.error("param decrypt failed, returns the origin value ......");
            return param;
        }
    }

    /**
     * 加密参数
     *
     * @param param 加密参数
     * @return 解密结果
     */
    public static String encrypt(String param) {
        try {
            return DataSecurityHelper.encrypt(param);
        } catch (Exception e) {
            log.error("param encrypt failed, returns the origin value ......");
            return param;
        }
    }
}
