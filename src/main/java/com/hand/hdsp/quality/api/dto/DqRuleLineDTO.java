package com.hand.hdsp.quality.api.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.choerodon.mybatis.domain.AuditDomain;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * <p>数据质量监控规则表 数据传输对象</p>
 *
 * @author jinbiao.liu@hand-china.com 2020-03-22 16:38:36
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@ApiModel("数据质量监控规则表")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DqRuleLineDTO extends AuditDomain {

    @ApiModelProperty("表ID，主键，供其他表做外键")
    private Long lineId;

    @ApiModelProperty(value = "规则ID")
    @NotNull
    private Long ruleId;

    @ApiModelProperty(value = "告警等级")
    @NotBlank
    private String alertLevel;

    @ApiModelProperty(value = "告警规则说明")
    private String alertRuleDesc;

    @ApiModelProperty(value = "上次告警时间")
    private Date lastAlarmTime;

    @ApiModelProperty(value = "下次告警时间")
    private Date nextAlarmTime;

    @ApiModelProperty(value = "是否启用 1:启用 0：不启用")
    @NotNull
    private Integer enabledFlag;

    @ApiModelProperty(value = "租户ID")
    @NotNull
    private Long tenantId;

    @ApiModelProperty(value = "")
    private String attributeCategory;

    @ApiModelProperty(value = "")
    private String attribute1;

    @ApiModelProperty(value = "")
    private String attribute2;

    @ApiModelProperty(value = "")
    private String attribute3;

    @ApiModelProperty(value = "")
    private String attribute4;

    @ApiModelProperty(value = "")
    private String attribute5;

    @ApiModelProperty(value = "")
    private String attribute6;

    @ApiModelProperty(value = "")
    private String attribute7;

    @ApiModelProperty(value = "")
    private String attribute8;

    @ApiModelProperty(value = "")
    private String attribute9;

    @ApiModelProperty(value = "")
    private String attribute10;

    @ApiModelProperty(value = "")
    private String attribute11;

    @ApiModelProperty(value = "")
    private String attribute12;

    @ApiModelProperty(value = "")
    private String attribute13;

    @ApiModelProperty(value = "")
    private String attribute14;

    @ApiModelProperty(value = "")
    private String attribute15;

    /**
     * 规则编码
     */
    @ApiModelProperty(value = "规则编码")
    private String ruleCode;

    @ApiModelProperty(value = "配置代码")
    private String configCode;

    @ApiModelProperty(value = "告警方式")
    private String alarmWay;

    @ApiModelProperty(value = "接收组")
    private String receiveGroupCode;

    @ApiModelProperty(value = "消息模板")
    private String messageTemplateCode;

}
