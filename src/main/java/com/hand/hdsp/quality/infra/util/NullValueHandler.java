package com.hand.hdsp.quality.infra.util;

import com.hand.hdsp.quality.api.dto.*;
import com.hand.hdsp.quality.infra.constant.PlanConstant;
import com.hand.hdsp.quality.infra.constant.WarningLevel;
import org.hzero.starter.driver.core.infra.util.JsonUtil;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

/**
 * @Author: dongwl
 * @Date: 2022/06/24
 */
@Component
public class NullValueHandler implements StandardHandler {

    @Override
    public BatchPlanFieldLineDTO handle(DataStandardDTO dataStandardDTO) {
        if (dataStandardDTO.getNullFlag() == 1 || dataStandardDTO.getNullFlag() == null) {
            return null;
        }
        BatchPlanFieldLineDTO batchPlanFieldLineDTO = BatchPlanFieldLineDTO.builder()
                .checkWay(PlanConstant.CheckWay.COMMON)
                .checkItem(PlanConstant.CheckItem.FIELD_EMPTY)
                .countType(PlanConstant.CountType.FIXED_VALUE)
                .fieldName(dataStandardDTO.getStandardCode() + '(' + dataStandardDTO.getDataType() + ')')
                .build();
        WarningLevelDTO warningLevelDTO = WarningLevelDTO.builder()
                .warningLevel(WarningLevel.ORANGE)
                .compareSymbol(PlanConstant.CompareSymbol.NOT_EQUAL)
                .expectedValue("0")
                .build();
        List<WarningLevelDTO> warningLevelDTOList = Collections.singletonList(warningLevelDTO);
        String warningLevel = JsonUtil.toJson(warningLevelDTOList);
        BatchPlanFieldConDTO batchPlanFieldConDTO = BatchPlanFieldConDTO.builder()
                .compareWay(PlanConstant.CompareWay.VALUE)
                .warningLevel(warningLevel)
                .warningLevelList(warningLevelDTOList)
                .build();
        batchPlanFieldLineDTO.setBatchPlanFieldConDTOList(Collections.singletonList(batchPlanFieldConDTO));
        return batchPlanFieldLineDTO;
    }

    @Override
    public BatchPlanFieldLineDTO handle(DataFieldDTO dataFieldDTO, String fieldType) {
        if (dataFieldDTO.getNullFlag() != 0) {
            return null;
        }
        // 创建校验项
        BatchPlanFieldLineDTO batchPlanFieldLineDTO = BatchPlanFieldLineDTO.builder()
                .checkWay(PlanConstant.CheckWay.COMMON)
                .checkItem(PlanConstant.CheckItem.FIELD_EMPTY)
                .countType(PlanConstant.CountType.FIXED_VALUE)
                .fieldName(dataFieldDTO.getFieldName() + '(' + fieldType + ')')
                .build();
        // 告警规则
        WarningLevelDTO warningLevelDTO = WarningLevelDTO.builder()
                .warningLevel(WarningLevel.ORANGE)
                .compareSymbol(PlanConstant.CompareSymbol.NOT_EQUAL)
                .expectedValue("0")
                .build();
        List<WarningLevelDTO> warningLevelDTOList = Collections.singletonList(warningLevelDTO);
        String warningLevel = JsonUtil.toJson(warningLevelDTOList);
        // 创建校验项下的配置项
        BatchPlanFieldConDTO batchPlanFieldConDTO = BatchPlanFieldConDTO.builder()
                .planLineId(batchPlanFieldLineDTO.getPlanLineId())
                .compareWay(PlanConstant.CompareWay.VALUE)
                .warningLevel(warningLevel)
                .warningLevelList(warningLevelDTOList)
                .build();
        batchPlanFieldLineDTO.setBatchPlanFieldConDTOList(Collections.singletonList(batchPlanFieldConDTO));
        return batchPlanFieldLineDTO;
    }
}
