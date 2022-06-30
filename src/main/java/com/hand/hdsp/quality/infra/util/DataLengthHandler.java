package com.hand.hdsp.quality.infra.util;

import static com.hand.hdsp.quality.infra.constant.StandardConstant.LengthType.FIXED;
import static com.hand.hdsp.quality.infra.constant.StandardConstant.LengthType.RANGE;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import com.hand.hdsp.quality.api.dto.*;
import com.hand.hdsp.quality.infra.constant.PlanConstant;
import com.hand.hdsp.quality.infra.constant.WarningLevel;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.logging.log4j.util.Strings;
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
        if (Strings.isEmpty(dataStandardDTO.getDataLength())) {
            return null;
        }
        BatchPlanFieldLineDTO batchPlanFieldLineDTO = BatchPlanFieldLineDTO.builder()
                .checkWay(PlanConstant.CheckWay.COMMON)
                .checkItem(PlanConstant.CheckItem.DATA_LENGTH)
                .build();
        BatchPlanFieldConDTO batchPlanFieldConDTO = null;
        List<WarningLevelDTO> warningLevelDTOList = null;
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
                WarningLevelDTO firstWarningLevelDTO = null;
                if (dataLengthList.get(0) > 0) {
                    firstWarningLevelDTO = WarningLevelDTO.builder()
                            .warningLevel(WarningLevel.ORANGE)
                            .startValue("0")
                            .endValue(String.valueOf(dataLengthList.get(0)))
                            .compareSymbol(PlanConstant.CompareSymbol.EQUAL)
                            .build();
                }
                WarningLevelDTO secondWarningLevelDTO = WarningLevelDTO.builder()
                        .warningLevel(WarningLevel.ORANGE)
                        .startValue(String.valueOf(dataLengthList.get(1)))
                        .compareSymbol(PlanConstant.CompareSymbol.EQUAL)
                        .build();
                if (Objects.isNull(firstWarningLevelDTO)) {
                    warningLevelDTOList = Collections.singletonList(secondWarningLevelDTO);
                } else {
                    warningLevelDTOList = Arrays.asList(firstWarningLevelDTO
                            , secondWarningLevelDTO);
                }
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
        return batchPlanFieldLineDTO;

    }

    @Override
    public BatchPlanFieldLineDTO handle(DataFieldDTO dataFieldDTO, String fieldType) {
        if (Strings.isEmpty(dataFieldDTO.getFieldLength())) {
            return null;
        }
        // 创建校验项
        BatchPlanFieldLineDTO batchPlanFieldLineDTO = BatchPlanFieldLineDTO.builder()
                .checkWay(PlanConstant.CheckWay.COMMON)
                .checkItem(PlanConstant.CheckItem.DATA_LENGTH)
                .fieldName(dataFieldDTO.getFieldName() + '(' + fieldType + ')')
                .build();
        List<WarningLevelDTO> warningLevelDTOList;
        // 生成告警规则
        String warningLevel = "";
        WarningLevelDTO warningLevelDTO = null;
        if (Integer.parseInt(dataFieldDTO.getFieldLength()) > 0) {
            warningLevelDTO = WarningLevelDTO.builder()
                    .warningLevel(WarningLevel.ORANGE)
                    .startValue(dataFieldDTO.getFieldLength())
                    .compareSymbol(PlanConstant.CompareSymbol.INCLUDED)
                    .build();
        }
        warningLevelDTOList = Collections.singletonList(warningLevelDTO);
        warningLevel = JsonUtil.toJson(warningLevelDTOList);

        // 生成配置项
        BatchPlanFieldConDTO batchPlanFieldConDTO = BatchPlanFieldConDTO.builder()
                .warningLevel(warningLevel)
                .warningLevelList(warningLevelDTOList)
                .compareWay(PlanConstant.CompareWay.RANGE)
                .build();
        batchPlanFieldLineDTO.setBatchPlanFieldConDTOList(Collections.singletonList(batchPlanFieldConDTO));
        return batchPlanFieldLineDTO;
    }

    private void convertToDataLengthList(DataStandardDTO dataStandardDTO) {
        //对数据长度进行处理
        if (Objects.nonNull(dataStandardDTO.getDataLength())) {
            List<String> dataLength = Arrays.asList(dataStandardDTO.getDataLength().split(","));
            List<Long> dataLengthList = dataLength.stream().map(Long::parseLong).collect(Collectors.toList());
            dataStandardDTO.setDataLengthList(dataLengthList);
        }
    }
}
