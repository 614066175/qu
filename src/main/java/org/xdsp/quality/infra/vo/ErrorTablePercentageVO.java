package org.xdsp.quality.infra.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import org.hzero.boot.platform.lov.annotation.LovValue;
import org.xdsp.quality.infra.constant.PlanConstant;

import java.math.BigDecimal;

/**
 * description
 *
 * @author rui.jia01@hand-china.com 2020/04/01 14:22
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@JsonInclude(value = JsonInclude.Include.NON_NULL)
public class ErrorTablePercentageVO {

    private String objectName;

    @LovValue(lovCode = PlanConstant.LOV_CHECK_ITEM, meaningField = "checkItemMeaning")
    private String checkItem;

    private String checkItemMeaning;

    @LovValue(lovCode = PlanConstant.LOV_COUNT_TYPE, meaningField = "countTypeMeaning")
    private String countType;

    private String countTypeMeaning;

    private BigDecimal percentage;

    private BigDecimal countSum;

}
