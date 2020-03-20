package com.hand.hdsp.quantity.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.service.Tag;
import springfox.documentation.spring.web.plugins.Docket;

/**
 * Swagger Api 描述配置
 *
 * @author feng.liu01@hand-china.com
 */
@Configuration
public class SwaggerTags {

    public static final String EXAMPLE = "Example";

    @Autowired
    public SwaggerTags(Docket docket) {
        docket.tags(
                new Tag(EXAMPLE, "EXAMPLE 案例")
        );
    }
}
