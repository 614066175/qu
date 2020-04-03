package com.hand.hdsp.quality.api.dto;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.choerodon.mybatis.domain.AuditDomain;
import lombok.*;

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

}
