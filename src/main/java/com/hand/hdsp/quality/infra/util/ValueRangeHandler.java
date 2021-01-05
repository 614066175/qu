package com.hand.hdsp.quality.infra.util;

import static com.hand.hdsp.quality.infra.constant.PlanConstant.CompareWay.RANGE;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import com.hand.hdsp.quality.api.dto.BatchPlanFieldConDTO;
import com.hand.hdsp.quality.api.dto.BatchPlanFieldLineDTO;
import com.hand.hdsp.quality.api.dto.DataStandardDTO;
import com.hand.hdsp.quality.api.dto.WarningLevelDTO;
import com.hand.hdsp.quality.infra.constant.PlanConstant;
import com.hand.hdsp.quality.infra.constant.WarningLevel;
import org.apache.commons.collections4.CollectionUtils;
import org.hzero.starter.driver.core.infra.util.JsonUtil;
import org.springframework.stereotype.Component;

/**
 * <p>
 * description
 * </p>
 *
 * @author lgl 2020/12/10 15:08
 * @since 1.0
 */
@Component
public class ValueRangeHandler implements StandardHandler {

    @Override
    public BatchPlanFieldLineDTO handle(DataStandardDTO dataStandardDTO) {
        BatchPlanFieldLineDTO batchPlanFieldLineDTO = BatchPlanFieldLineDTO.builder()
                .checkWay(PlanConstant.CheckWay.COMMON)
                .checkItem(PlanConstant.CheckItem.FIELD_VALUE)
                .build();
        //判断类型，并生成不同的配置项告警规则
        String warningLevel = "";
        WarningLevelDTO warningLevelDTO;
        List<WarningLevelDTO> warningLevelDTOList = null;
        switch (dataStandardDTO.getValueType()) {
            case PlanConstant.StandardValueType.AREA:
                batchPlanFieldLineDTO.setCountType(PlanConstant.CountType.FIXED_VALUE);
                List<String> valueRangeList = Arrays.asList(dataStandardDTO.getValueRange().split(","));
                if (CollectionUtils.isNotEmpty(valueRangeList)
                        && valueRangeList.size() == 2) {
                    WarningLevelDTO firstWarningLevelDTO = null;
                    if (Integer.parseInt(valueRangeList.get(0)) > 0) {
                        firstWarningLevelDTO = WarningLevelDTO.builder()
                                .warningLevel(WarningLevel.ORANGE)
                                .startValue("0")
                                .endValue(valueRangeList.get(0))
                                .compareSymbol(PlanConstant.CompareSymbol.EQUAL)
                                .build();
                    }
                    WarningLevelDTO secondWarningLevelDTO = WarningLevelDTO.builder()
                            .warningLevel(WarningLevel.ORANGE)
                            .startValue(valueRangeList.get(1))
                            .compareSymbol(PlanConstant.CompareSymbol.EQUAL)
                            .build();
                    if(Objects.isNull(firstWarningLevelDTO)){
                        warningLevelDTOList=Collections.singletonList(secondWarningLevelDTO);
                    }else{
                        warningLevelDTOList = Arrays.asList(firstWarningLevelDTO
                                , secondWarningLevelDTO);
                    }
                    warningLevel = JsonUtil.toJson(warningLevelDTOList);
                }
                break;
            case PlanConstant.StandardValueType.ENUM:
                batchPlanFieldLineDTO.setCountType(PlanConstant.CountType.ENUM_VALUE);
                warningLevelDTO = WarningLevelDTO.builder()
                        .warningLevel(WarningLevel.ORANGE)
                        .compareSymbol(PlanConstant.CompareSymbol.INCLUDED)
                        .enumValue(dataStandardDTO.getValueRange())
                        .build();
                warningLevelDTOList = Collections.singletonList(warningLevelDTO);
                warningLevel = JsonUtil.toJson(warningLevelDTOList);
                break;
            case PlanConstant.StandardValueType.VALUE_SET:
                batchPlanFieldLineDTO.setCountType(PlanConstant.CountType.LOV_VALUE);
                warningLevelDTO = WarningLevelDTO.builder()
                        .warningLevel(WarningLevel.ORANGE)
                        .compareSymbol(PlanConstant.CompareSymbol.INCLUDED)
                        .enumValue(dataStandardDTO.getValueRange())
                        .build();
                warningLevelDTOList = Collections.singletonList(warningLevelDTO);
                warningLevel = JsonUtil.toJson(warningLevelDTOList);
                break;
            default:
                break;
        }
        //生成每个校验项的配置项
        BatchPlanFieldConDTO batchPlanFieldConDTO = BatchPlanFieldConDTO.builder()
                .planLineId(batchPlanFieldLineDTO.getPlanLineId())
                .warningLevel(warningLevel)
                .warningLevelList(warningLevelDTOList)
                .build();
        //如果校验类型为固定值，默认给范围比较
        if (PlanConstant.CountType.FIXED_VALUE.equals(batchPlanFieldLineDTO.getCountType())) {
            batchPlanFieldConDTO.setCompareWay(RANGE);
        }
        batchPlanFieldLineDTO.setBatchPlanFieldConDTOList(Collections.singletonList(batchPlanFieldConDTO));
        return batchPlanFieldLineDTO;
    }
}
