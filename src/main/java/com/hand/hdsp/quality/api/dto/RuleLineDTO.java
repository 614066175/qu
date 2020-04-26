package com.hand.hdsp.quality.api.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.choerodon.mybatis.domain.AuditDomain;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import org.hzero.boot.platform.lov.annotation.LovValue;

import javax.persistence.Id;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * 规则校验项表 数据传输对象
 *
 * @author wuzhong26857
 * @version 1.0
 * @date 2020/3/23 13:18
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@ApiModel("规则校验项表")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RuleLineDTO extends AuditDomain {

    @ApiModelProperty("表ID，主键，供其他表做外键")
    @Id
    private Long ruleLineId;

    @ApiModelProperty("规则表XQUA_RULE.RULE_ID")
    @NotNull
    private Long ruleId;

    @ApiModelProperty("校验方式")
    @LovValue(lovCode = "HDSP.XQUA.CHECK_WAY", meaningField = "checkWayMeaning")
    private String checkWay;

    @ApiModelProperty("校验项")
    @LovValue(lovCode = "HDSP.XQUA.CHECK_ITEM", meaningField = "checkItemMeaning")
    private String checkItem;

    @ApiModelProperty("比较方式")
    @LovValue(lovCode = "HDSP.XQUA.COMPARE_WAY", meaningField = "compareWayMeaning")
    private String compareWay;

    @ApiModelProperty("阈值")
    private String expectedValue;

    @ApiModelProperty("正则表达式")
    private String regularExpression;

    @ApiModelProperty("租户ID")
    @NotNull
    private Long tenantId;

    @ApiModelProperty("规则告警等级List")
    private List<RuleWarningLevelDTO> ruleWarningLevelDTOList;
}
