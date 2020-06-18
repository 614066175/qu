package com.hand.hdsp.quality.api.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.hand.hdsp.quality.infra.constant.PlanConstant;
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
    @LovValue(lovCode = PlanConstant.LOV_CHECK_ITEM, meaningField = "checkItemMeaning")
    private String checkItem;

    @ApiModelProperty("校验类型 HDSP.XQUA.COUNT_TYPE")
    @LovValue(lovCode = PlanConstant.LOV_COUNT_TYPE, meaningField = "countTypeMeaning")
    private String countType;

    @ApiModelProperty("比较方式")
    @LovValue(lovCode = "HDSP.XQUA.COMPARE_WAY", meaningField = "compareWayMeaning")
    private String compareWay;

    @ApiModelProperty("正则表达式")
    private String regularExpression;

    @ApiModelProperty(value = "告警等级json")
    private String warningLevel;

    @ApiModelProperty("租户ID")
    @NotNull
    private Long tenantId;

    @ApiModelProperty(value = "方案告警等级List")
    private List<WarningLevelDTO> warningLevelList;
}
