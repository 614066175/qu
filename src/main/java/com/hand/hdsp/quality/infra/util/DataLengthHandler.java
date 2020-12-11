package com.hand.hdsp.quality.infra.util;

import static com.hand.hdsp.quality.infra.constant.StandardConstant.LengthType.FIXED;
import static com.hand.hdsp.quality.infra.constant.StandardConstant.LengthType.RANGE;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

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
public class DataLengthHandler implements StandardHandler {

    @Override
    public BatchPlanFieldLineDTO handle(DataStandardDTO dataStandardDTO) {
        BatchPlanFieldLineDTO batchPlanFieldLineDTO = BatchPlanFieldLineDTO.builder()
                .checkWay(PlanConstant.CheckWay.COMMON)
                .checkItem(PlanConstant.CheckItem.DATA_LENGTH)
                .build();
        BatchPlanFieldConDTO batchPlanFieldConDTO = null;
        List<WarningLevelDTO> warningLevelDTOList=null;
        if (FIXED.equals(dataStandardDTO.getLengthType())) {
            batchPlanFieldLineDTO.setCountType(PlanConstant.CountType.FIXED_VALUE);
            //生成每个校验项的配置项
            WarningLevelDTO warningLevelDTO = WarningLevelDTO.builder()
                    .warningLevel(WarningLevel.ORANGE)
                    .compareSymbol(PlanConstant.CompareSymbol.NOT_EQUAL)
                    .expectedValue(dataStandardDTO.getDataLength())
                    .build();
            warningLevelDTOList = Collections.singletonList(warningLevelDTO);
            String warningLevel = JsonUtil.toJson(warningLevelDTOList);
            batchPlanFieldConDTO = BatchPlanFieldConDTO.builder()
                    .planLineId(batchPlanFieldLineDTO.getPlanLineId())
                    .warningLevel(warningLevel)
                    .compareWay(PlanConstant.CompareWay.VALUE)
                    .build();
        }
        if (RANGE.equals(dataStandardDTO.getLengthType())) {
            convertToDataLengthList(dataStandardDTO);
            batchPlanFieldLineDTO.setCountType(PlanConstant.CountType.LENGTH_RANGE);
            //生成每个校验项的配置项
            //两个值都存在生成告警规则
            List<Long> dataLengthList = dataStandardDTO.getDataLengthList();
            String warningLevel = "";
            if (CollectionUtils.isNotEmpty(dataLengthList)
                    && dataLengthList.size() == 2) {
                WarningLevelDTO firstWarningLevelDTO = WarningLevelDTO.builder()
                        .warningLevel(WarningLevel.ORANGE)
                        .startValue("0")
                        .endValue(String.valueOf(dataLengthList.get(0) - 1))
                        .compareSymbol(PlanConstant.CompareSymbol.EQUAL)
                        .build();
                WarningLevelDTO secondWarningLevelDTO = WarningLevelDTO.builder()
                        .warningLevel(WarningLevel.ORANGE)
                        .startValue(String.valueOf(dataLengthList.get(1) + 1))
                        .compareSymbol(PlanConstant.CompareSymbol.EQUAL)
                        .build();
                warningLevelDTOList = Arrays.asList(firstWarningLevelDTO
                        , secondWarningLevelDTO);
                warningLevel = JsonUtil.toJson(warningLevelDTOList);
            }
            batchPlanFieldConDTO = BatchPlanFieldConDTO.builder()
                    .planLineId(batchPlanFieldLineDTO.getPlanLineId())
                    .warningLevel(warningLevel)
                    .warningLevelList(warningLevelDTOList)
                    .compareWay(PlanConstant.CompareWay.RANGE)
                    .build();
        }
        batchPlanFieldLineDTO.setBatchPlanFieldConDTOList(Collections.singletonList(batchPlanFieldConDTO));
        return  batchPlanFieldLineDTO;

    }

    private void convertToDataLengthList(DataStandardDTO dataStandardDTO) {
        //对数据长度进行处理
        if (dataStandardDTO.getDataLength() != null) {
            List<String> dataLength = Arrays.asList(dataStandardDTO.getDataLength().split(","));
            List<Long> dataLengthList = dataLength.stream().map(Long::parseLong).collect(Collectors.toList());
            dataStandardDTO.setDataLengthList(dataLengthList);
        }
    }
}
