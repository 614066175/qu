package com.hand.hdsp.quality.infra.util;

import java.util.Set;

/**
 * @Title: ValidatorUtil
 * @Description:
 * @author: lgl
 * @date: 2022/10/23 19:15
 */
public class ValidatorUtil {


    public static final ThreadLocal<Set<String>> groupCodeThreadLocal=new InheritableThreadLocal();

    public static ThreadLocal<Set<String>> getGroupCodeThreadLocal() {
        return groupCodeThreadLocal;
    }


}
