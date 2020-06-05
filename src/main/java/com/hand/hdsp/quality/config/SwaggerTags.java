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
    public static final String RULE_GROUP_SITE = "RULE GROUP SITE CONTROLLER";
    public static final String RULE = "RULE CONTROLLER";
    public static final String RULE_SITE = "RULE SITE CONTROLLER";
    public static final String RULE_LINE = "RULE LINE CONTROLLER";
    public static final String RULE_LINE_SITE = "RULE LINE SITE CONTROLLER";
    public static final String RULE_WARNING_LEVEL = "RULE WARNING LEVEL CONTROLLER";
    public static final String RULE_WARNING_LEVEL_SITE = "RULE WARNING LEVEL SITE CONTROLLER";
    public static final String BATCH_PLAN = "BATCH PLAN CONTROLLER";
    public static final String BATCH_PLAN_BASE = "BATCH PLAN BASE CONTROLLER";
    public static final String BATCH_PLAN_TABLE = "BATCH PLAN TABLE CONTROLLER";
    public static final String BATCH_PLAN_TABLE_LINE = "BATCH PLAN TABLE LINE CONTROLLER";
    public static final String BATCH_PLAN_TABLE_CON = "BATCH PLAN TABLE CON CONTROLLER";
    public static final String BATCH_PLAN_FIELD = "BATCH PLAN FIELD CONTROLLER";
    public static final String BATCH_PLAN_FIELD_LINE = "BATCH PLAN FIELD LINE CONTROLLER";
    public static final String BATCH_PLAN_FIELD_CON = "BATCH PLAN FIELD CON CONTROLLER";
    public static final String BATCH_PLAN_REL_TABLE = "BATCH PLAN REL TABLE CONTROLLER";
    public static final String BATCH_RESULT = "BATCH RESULT";
    public static final String BATCH_RESULT_BASE = "BATCH RESULT BASE";
    public static final String BATCH_RESULT_RULE = "BATCH RESULT RULE";
    public static final String PLAN_GROUP = "PLAN GROUP CONTROLLER";
    public static final String STREAMING_PLAN = "STREAMING PLAN CONTROLLER";
    public static final String STREAMING_PLAN_BASE = "STREAMING PLAN BASE CONTROLLER";
    public static final String STREAMING_PLAN_RULE = "STREAMING PLAN RULE CONTROLLER";
    public static final String STREAMING_RESULT = "STREAMING RESULT";
    public static final String STREAMING_RESULT_BASE = "STREAMING RESULT BASE";
    public static final String STREAMING_RESULT_RULE = "STREAMING RESULT RULE";

    @Autowired
    public SwaggerTags(Docket docket) {
        docket.tags(
                new Tag(RULE_GROUP, "规则分组表 管理 API"),
                new Tag(RULE_GROUP_SITE, "规则分组表（平台级） 管理 API"),
                new Tag(RULE, "规则表 管理 API"),
                new Tag(RULE_SITE, "规则表（平台级） 管理 API"),
                new Tag(RULE_LINE, "规则校验项表 管理 API"),
                new Tag(RULE_LINE_SITE, "规则校验项表（平台级） 管理 API"),
                new Tag(RULE_WARNING_LEVEL, "规则告警等级表 管理 API"),
                new Tag(RULE_WARNING_LEVEL_SITE, "规则告警等级表（平台级） 管理 API"),
                new Tag(BATCH_PLAN, "批数据评估方案表 管理 API"),
                new Tag(BATCH_PLAN_BASE, "批数据方案-基础配置表 管理 API"),
                new Tag(BATCH_PLAN_TABLE, "批数据方案-表级规则表 管理 API"),
                new Tag(BATCH_PLAN_TABLE_LINE, "批数据方案-表级规则校验项表 管理 API"),
                new Tag(BATCH_PLAN_TABLE_CON, "批数据方案-表级规则条件表 管理 API"),
                new Tag(BATCH_PLAN_FIELD, "批数据方案-字段规则表 管理 API"),
                new Tag(BATCH_PLAN_FIELD_LINE, "批数据方案-字段规则校验项表 管理 API"),
                new Tag(BATCH_PLAN_FIELD_CON, "批数据方案-字段规则条件表 管理 API"),
                new Tag(BATCH_PLAN_REL_TABLE, "批数据方案-表间规则表 管理 API"),
                new Tag(BATCH_RESULT, "批数据方案结果表 管理 API"),
                new Tag(BATCH_RESULT_BASE, "批数据方案结果表-表信息 管理 API"),
                new Tag(BATCH_RESULT_RULE, "批数据方案结果表-规则信息 管理 API"),
                new Tag(PLAN_GROUP, "评估方案分组表 管理 API"),
                new Tag(STREAMING_PLAN, "实时数据评估方案表 管理 API"),
                new Tag(STREAMING_PLAN_BASE, "实时数据方案-基础配置表 管理 API"),
                new Tag(STREAMING_PLAN_RULE, "实时数据方案-规则表 管理 API"),
                new Tag(STREAMING_RESULT, "实时数据方案结果表 管理 API"),
                new Tag(STREAMING_RESULT_BASE, "实时数据方案结果表-基础信息 管理 API"),
                new Tag(STREAMING_RESULT_RULE, "实时数据方案结果表-规则信息 管理 API")
        );
    }
}
