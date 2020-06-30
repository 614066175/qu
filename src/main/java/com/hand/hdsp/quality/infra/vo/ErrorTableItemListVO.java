package com.hand.hdsp.quality.infra.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.hand.hdsp.quality.api.dto.WarningLevelDTO;
import com.hand.hdsp.quality.infra.constant.PlanConstant;
import lombok.*;
import org.hzero.boot.platform.lov.annotation.LovValue;

import java.util.Date;
import java.util.List;

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
public class ErrorTableItemListVO {

    @LovValue(lovCode = PlanConstant.LOV_CHECK_ITEM, meaningField = "checkItemMeaning")
    private String checkItem;

    private String checkItemMeaning;

    @LovValue(lovCode = PlanConstant.LOV_COUNT_TYPE, meaningField = "countTypeMeaning")
    private String countType;

    private String countTypeMeaning;

    private String whereCondition;

    private String compareWay;

    private String exceptionInfo;

    private String warningLevel;

    private String actualValue;

    private String ruleName;

    private String planName;

    private Date startDate;

    private String realName;

    private String warningLevelJson;

    private List<WarningLevelDTO> warningLevelList;
}
