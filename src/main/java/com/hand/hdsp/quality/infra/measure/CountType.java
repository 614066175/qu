package com.hand.hdsp.quality.infra.measure;

import org.springframework.stereotype.Component;

import java.lang.annotation.*;

/**
 * <p>校验类型</p>
 *
 * @author feng.liu01@hand-china.com 2020-03-24 16:19:53
 */
@Component
@Inherited
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface CountType {

    /**
     * 校验项
     *
     * @return 校验项
     */
    String value();

}
