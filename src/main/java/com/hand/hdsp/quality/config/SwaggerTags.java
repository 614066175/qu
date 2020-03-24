package com.hand.hdsp.quality.config;

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

    public static final String RULE_GROUP = "RULE GROUP CONTROLLER";
    public static final String RULE = "RULE CONTROLLER";
    public static final String RULE_LINE = "RULE LINE CONTROLLER";
    public static final String RULE_WARNING_LEVEL = "RULEWARNINGLEVEL CONTROLLER";

    @Autowired
    public SwaggerTags(Docket docket) {
        docket.tags(
                new Tag(RULE_GROUP, "规则分组表 管理 API"),
                new Tag(RULE, "规则表 管理 API"),
                new Tag(RULE_LINE, "规则校验项表 管理 API"),
                new Tag(RULE_WARNING_LEVEL, "规则告警等级表 管理 API")
        );
    }
}
