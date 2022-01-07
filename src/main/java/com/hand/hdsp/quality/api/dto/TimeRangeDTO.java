package com.hand.hdsp.quality.api.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.choerodon.mybatis.domain.AuditDomain;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import java.util.Date;

/**
 * description
 *
 * @author rui.jia01@hand-china.com 2020/04/03 17:19
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TimeRangeDTO extends AuditDomain {

    private Long tenantId;

    /**
     * 前端可能传的时间条件
     */
    private String timeRange;

    /**
     * 前端可能传的起始时间
     */
    private Date start;

    /**
     * 前端可能传的截止时间
     */
    private Date end;

    /**
     * 后台处理后供查询的起始时间
     */
    private String startDate;

    /**
     * 后台处理后供查询的截止时间
     */
    private String endDate;

    private String rule;

    private String topicInfo;

    @ApiModelProperty(value = "数据源ID")
    private Long datasourceId;

    @ApiModelProperty(value = "数据库")
    private String datasourceSchema;

    @ApiModelProperty(value = "表名/视图名/自定义SQL")
    private String objectName;

    private String ruleType;

    /**
     * 问题数据趋势
     */
    private Long planBaseId;

    private String dateGroup;

    private Long problemDataCount;

    private String warningLevel;

    private Long projectId;

}
