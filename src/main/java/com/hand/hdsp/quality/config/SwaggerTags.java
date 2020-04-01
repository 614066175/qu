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
    public static final String RULE_WARNING_LEVEL = "RULE WARNING LEVEL CONTROLLER";
    public static final String BATCH_PLAN = "BATCH PLAN CONTROLLER";
    public static final String BATCH_PLAN_BASE = "BATCH PLAN BASE CONTROLLER";
    public static final String BATCH_PLAN_TABLE = "BATCH PLAN TABLE CONTROLLER";
    public static final String BATCH_PLAN_TABLE_LINE = "BATCH PLAN TABLE LINE CONTROLLER";
    public static final String BATCH_PLAN_FIELD = "BATCH PLAN FIELD CONTROLLER";
    public static final String BATCH_PLAN_FIELD_LINE = "BATCH PLAN FIELD LINE CONTROLLER";
    public static final String BATCH_PLAN_REL_TABLE = "BATCH PLAN REL TABLE CONTROLLER";
    public static final String BATCH_PLAN_REL_TABLE_LINE = "BATCH PLAN REL TABLE LINE CONTROLLER";
    public static final String PLAN_GROUP = "PLAN GROUP CONTROLLER";
    public static final String PLAN_WARNING_LEVEL = "PLAN WARNING LEVEL CONTROLLER";
    public static final String STREAMING_PLAN = "STREAMING PLAN CONTROLLER";
    public static final String STREAMING_PLAN_BASE = "STREAMING PLAN BASE CONTROLLER";
    public static final String STREAMING_PLAN_RULE = "STREAMING PLAN RULE CONTROLLER";

    @Autowired
    public SwaggerTags(Docket docket) {
        docket.tags(
                new Tag(RULE_GROUP, "规则分组表 管理 API"),
                new Tag(RULE, "规则表 管理 API"),
                new Tag(RULE_LINE, "规则校验项表 管理 API"),
                new Tag(RULE_WARNING_LEVEL, "规则告警等级表 管理 API"),
                new Tag(BATCH_PLAN, "批数据评估方案表 管理 API"),
                new Tag(BATCH_PLAN_BASE, "批数据方案-基础配置表 管理 API"),
                new Tag(BATCH_PLAN_TABLE, "批数据方案-表级规则表 管理 API"),
                new Tag(BATCH_PLAN_TABLE_LINE, "批数据方案-表级规则校验项表 管理 API"),
                new Tag(BATCH_PLAN_FIELD, "批数据方案-字段规则表 管理 API"),
                new Tag(BATCH_PLAN_FIELD_LINE, "批数据方案-字段规则校验项表 管理 API"),
                new Tag(BATCH_PLAN_REL_TABLE, "批数据方案-表间规则表 管理 API"),
                new Tag(BATCH_PLAN_REL_TABLE_LINE, "批数据方案-表间规则关联关系表 管理 API"),
                new Tag(PLAN_GROUP, "评估方案分组表 管理 API"),
                new Tag(PLAN_WARNING_LEVEL, "方案告警等级表 管理 API"),
                new Tag(STREAMING_PLAN, "实时数据评估方案表 管理 API"),
                new Tag(STREAMING_PLAN_BASE, "实时数据方案-基础配置表 管理 API"),
                new Tag(STREAMING_PLAN_RULE, "实时数据方案-规则表 管理 API")
        );
    }
}
