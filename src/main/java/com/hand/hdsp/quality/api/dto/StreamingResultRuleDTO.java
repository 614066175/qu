package com.hand.hdsp.quality.api.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.choerodon.mybatis.annotation.ModifyAudit;
import io.choerodon.mybatis.annotation.VersionAudit;
import io.choerodon.mybatis.domain.AuditDomain;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * <p>实时数据方案结果表-规则信息 数据传输对象</p>
 *
 * @author feng.liu01@hand-china.com 2020-03-24 16:19:50
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@ApiModel("实时数据方案结果表-规则信息")
@JsonInclude(JsonInclude.Include.NON_NULL)
@VersionAudit
@ModifyAudit
public class StreamingResultRuleDTO extends AuditDomain {

    @ApiModelProperty("表ID，主键，供其他表做外键")
    private Long resultRuleId;

    @ApiModelProperty(value = "XQUA_STREAMING_RESULT_BASE.RESULT_BASE_ID")
    @NotNull
    private Long resultBaseId;

    @ApiModelProperty(value = "规则名称")
    private String ruleName;

    @ApiModelProperty(value = "topic信息")
    private String topicInfo;

    @ApiModelProperty(value = "规则类型 XQUA.RULE_TYPE")
    private String ruleType;

    @ApiModelProperty(value = "延迟/断流时间")
    private Date delayDate;

    @ApiModelProperty(value = "错误信息")
    private String exceptionInfo;

    @ApiModelProperty(value = "告警等级 XQUA.WARNING_LEVEL")
    private String warningLevel;

    @ApiModelProperty(value = "租户ID")
    @NotNull
    private Long tenantId;

    private Long projectId;
}
