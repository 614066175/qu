package com.hand.hdsp.quality.infra.util;

import java.util.Collections;
import java.util.List;

import com.hand.hdsp.quality.api.dto.BatchPlanFieldConDTO;
import com.hand.hdsp.quality.api.dto.BatchPlanFieldLineDTO;
import com.hand.hdsp.quality.api.dto.DataStandardDTO;
import com.hand.hdsp.quality.api.dto.WarningLevelDTO;
import com.hand.hdsp.quality.infra.constant.PlanConstant;
import com.hand.hdsp.quality.infra.constant.WarningLevel;
import org.hzero.starter.driver.core.infra.util.JsonUtil;
import org.springframework.stereotype.Component;

/**
 * <p>
 *
 * 数据格式转换处理器
 * description
 * </p>
 *
 * @author lgl 2020/12/10 14:53
 * @since 1.0
 */
@Component
public class DataPatternHandler implements StandardHandler {

    @Override
    public BatchPlanFieldLineDTO handle(DataStandardDTO dataStandardDTO) {

        BatchPlanFieldLineDTO batchPlanFieldLineDTO = BatchPlanFieldLineDTO.builder()
                .checkWay(PlanConstant.CheckWay.REGULAR)
                .checkItem(PlanConstant.CheckItem.REGULAR)
                .regularExpression(dataStandardDTO.getDataPattern())
                .build();

        WarningLevelDTO warningLevelDTO = WarningLevelDTO.builder()
                .warningLevel(WarningLevel.ORANGE)
                .compareSymbol(PlanConstant.CompareSymbol.EQUAL)
                .build();
        List<WarningLevelDTO> warningLevelDTOList = Collections.singletonList(warningLevelDTO);
        String warningLevel = JsonUtil.toJson(warningLevelDTOList);

        BatchPlanFieldConDTO batchPlanFieldConDTO = BatchPlanFieldConDTO.builder()
                .warningLevel(warningLevel)
                .warningLevelList(warningLevelDTOList)
                .build();

        batchPlanFieldLineDTO.setBatchPlanFieldConDTOList(Collections.singletonList(batchPlanFieldConDTO));
        return batchPlanFieldLineDTO;
    }
}
