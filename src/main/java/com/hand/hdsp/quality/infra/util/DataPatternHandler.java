package com.hand.hdsp.quality.infra.util;

import com.hand.hdsp.quality.api.dto.BatchPlanFieldConDTO;
import com.hand.hdsp.quality.api.dto.BatchPlanFieldLineDTO;
import com.hand.hdsp.quality.api.dto.DataStandardDTO;
import com.hand.hdsp.quality.api.dto.WarningLevelDTO;
import com.hand.hdsp.quality.infra.constant.PlanConstant;
import com.hand.hdsp.quality.infra.constant.WarningLevel;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.hzero.starter.driver.core.infra.util.JsonUtil;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

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
@Slf4j
public class DataPatternHandler implements StandardHandler {

    @Override
    public BatchPlanFieldLineDTO handle(DataStandardDTO dataStandardDTO) {
        if(Strings.isEmpty(dataStandardDTO.getDataPattern())){
            return null;
        }
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

    @Override
    public void valid(DataStandardDTO dataStandardDTO) {
        log.info("正则无需校验");
    }
}
