package com.hand.hdsp.quantity.infra.annotation;

import io.choerodon.resource.annoation.EnableChoerodonResourceServer;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

import java.lang.annotation.*;

/**
 * <p>组合启用集成注解</p>
 *
 * @author aaron.yi
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@EnableFeignClients
@EnableEurekaClient
@EnableChoerodonResourceServer
@MapperScan({
        "io.choerodon.**.mapper",
        "org.hzero.**.mapper",
        "com.hand.hdsp.**.mapper"
})
public @interface EnableQuantity {
}
