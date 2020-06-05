package com.hand.hdsp.quality.api.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.choerodon.mybatis.domain.AuditDomain;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import javax.validation.constraints.NotBlank;
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
@ApiModel("告警等级")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class WarningLevelDTO extends AuditDomain {

    @ApiModelProperty(value = "告警等级 HDSP.XQUA.WARNING_LEVEL")
    @NotBlank
    private String warningLevel;

    @ApiModelProperty(value = "阈值范围开始")
    private BigDecimal startValue;

    @ApiModelProperty(value = "阈值范围结束")
    private BigDecimal endValue;

    @ApiModelProperty(value = "比较符号 HDSP.XQUA.COMPARE_SYMBOL")
    private String compareSymbol;

    @ApiModelProperty(value = "阈值")
    private String expectedValue;
}
