package org.xdsp.quality.api.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.choerodon.mybatis.domain.AuditDomain;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import org.hzero.boot.platform.lov.annotation.LovValue;

import javax.validation.constraints.NotBlank;

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

    @ApiModelProperty(value = "告警等级 XQUA.WARNING_LEVEL")
    @NotBlank
    @LovValue(lovCode = "XQUA.WARNING_LEVEL", meaningField = "warningLevelMeaning")
    private String warningLevel;

    @ApiModelProperty(value = "阈值范围开始")
    private String startValue;

    @ApiModelProperty(value = "阈值范围结束")
    private String endValue;

    @ApiModelProperty(value = "比较符号 XQUA.COMPARE_SYMBOL")
    @LovValue(lovCode = "XQUA.COMPARE_SYMBOL", meaningField = "compareSymbolMeaning")
    private String compareSymbol;

    @ApiModelProperty(value = "阈值")
    private String expectedValue;

    @ApiModelProperty(value = "值集编码")
    @Builder.Default
    private String lovCode="XQUA.WARNING_LEVEL";

    @ApiModelProperty(value = "参考数据id")
    private Long referenceDataId;

    @ApiModelProperty(value = "枚举值")
    private String enumValue;

    private String warningLevelMeaning;

    private String compareSymbolMeaning;

    @ApiModelProperty(value = "参考数据名称")
    private String referenceDataName;

    @ApiModelProperty(value = "参考数据编码")
    private String referenceDataCode;

    /**
     * 标记是否触发改告警
     */
    @Builder.Default
    private Long ifAlert = 0L;
}
