package org.xdsp.quality.infra.util;

import org.hzero.starter.driver.core.infra.util.JsonUtil;
import org.springframework.stereotype.Component;
import org.xdsp.quality.api.dto.*;
import org.xdsp.quality.infra.constant.PlanConstant;
import org.xdsp.quality.infra.constant.WarningLevel;

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
        if (dataStandardDTO.getNullFlag() == null || dataStandardDTO.getNullFlag() == 1) {
            return null;
        }
        BatchPlanFieldLineDTO batchPlanFieldLineDTO = BatchPlanFieldLineDTO.builder()
                .checkWay(PlanConstant.CheckWay.COMMON)
                .checkItem(PlanConstant.CheckItem.FIELD_EMPTY)
                .countType(PlanConstant.CountType.FIXED_VALUE)
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
    public void valid(DataStandardDTO dataStandardDTO) {

    }

    @Override
    public BatchPlanFieldLineDTO handle(DataFieldDTO dataFieldDTO, String fieldType) {
        if (dataFieldDTO.getNullFlag() == null || dataFieldDTO.getNullFlag() == 1) {
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
