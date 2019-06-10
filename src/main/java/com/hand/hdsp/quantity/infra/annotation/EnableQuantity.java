package com.hand.hdsp.quantity.infra.annotation;

import io.choerodon.resource.annoation.EnableChoerodonResourceServer;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

import java.lang.annotation.*;

/**
 * <p>组合启用集成注解</p>
 *
 * @author aaron.yi
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@EnableEurekaClient
@EnableChoerodonResourceServer
@MapperScan({
        "com.hand.hdsp.quantity.**.mapper",
})
public @interface EnableQuantity {
}