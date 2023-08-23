package org.xdsp.quality.infra.annotation;

import io.choerodon.resource.annoation.EnableChoerodonResourceServer;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
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
@EnableDiscoveryClient
@EnableChoerodonResourceServer
@MapperScan({
        "io.choerodon.**.mapper",
        "org.hzero.**.mapper",
        "org.xdsp.**.mapper"
})
public @interface EnableQuality {
}
