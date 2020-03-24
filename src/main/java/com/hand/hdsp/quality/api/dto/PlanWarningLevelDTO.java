package com.hand.hdsp.quality.api.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.choerodon.mybatis.domain.AuditDomain;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.math.BigDecimal;

/**
 * <p>方案告警等级表 数据传输对象</p>
 *
 * @author feng.liu01@hand-china.com 2020-03-24 16:19:51
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@ApiModel("方案告警等级表")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PlanWarningLevelDTO extends AuditDomain {

    @ApiModelProperty("表ID，主键，供其他表做外键")
    private Long levelId;

    @ApiModelProperty(value = "业务主键")
    @NotNull
    private Long sourceId;

    @ApiModelProperty(value = "对应业务类型")
    @NotBlank
    @Size(max = 50)
    private String sourceType;

    @ApiModelProperty(value = "告警等级 HDSP.XQUA.WARNING_LEVEL")
    @NotBlank
    @Size(max = 30)
    private String warningLevel;

    @ApiModelProperty(value = "阈值范围开始")
    private BigDecimal startValue;

    @ApiModelProperty(value = "阈值范围结束")
    private BigDecimal endValue;

    @ApiModelProperty(value = "单位")
    private String unit;

    @ApiModelProperty(value = "租户ID")
    @NotNull
    private Long tenantId;

}
