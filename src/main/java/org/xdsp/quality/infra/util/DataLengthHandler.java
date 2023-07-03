package org.xdsp.quality.infra.util;

import io.choerodon.core.exception.CommonException;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.util.Strings;
import org.hzero.starter.driver.core.infra.util.JsonUtil;
import org.springframework.stereotype.Component;
import org.xdsp.quality.api.dto.*;
import org.xdsp.quality.infra.constant.ErrorCode;
import org.xdsp.quality.infra.constant.PlanConstant;
import org.xdsp.quality.infra.constant.WarningLevel;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static org.xdsp.quality.infra.constant.StandardConstant.LengthType.FIXED;
import static org.xdsp.quality.infra.constant.StandardConstant.LengthType.RANGE;

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
        if (StringUtils.isEmpty(dataStandardDTO.getDataLength()) || StringUtils.isEmpty(dataStandardDTO.getLengthType())) {
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
            if (CollectionUtils.isNotEmpty(dataLengthList)) {
                //如果是全闭区间
                if (dataLengthList.size() == 2) {
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
                if (dataLengthList.size() == 1) {
                    WarningLevelDTO warningLevelDTO = null;
                    //左开右闭
                    if (dataStandardDTO.getDataLength().startsWith(",")) {
                        warningLevelDTO = WarningLevelDTO.builder()
                                .warningLevel(WarningLevel.ORANGE)
                                .startValue(String.valueOf(dataLengthList.get(0)))
                                .compareSymbol(PlanConstant.CompareSymbol.EQUAL)
                                .build();
                    }
                    //左闭右开
                    else if (dataStandardDTO.getDataLength().endsWith(",")) {
                        warningLevelDTO = WarningLevelDTO.builder()
                                .warningLevel(WarningLevel.ORANGE)
                                .startValue("0")
                                .endValue(String.valueOf(dataLengthList.get(0)))
                                .compareSymbol(PlanConstant.CompareSymbol.EQUAL)
                                .build();
                    }
                    if (Objects.nonNull(warningLevelDTO)) {
                        warningLevelDTOList = Collections.singletonList(warningLevelDTO);
                    }
                    warningLevel = JsonUtil.toJson(warningLevelDTOList);
                }

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

    @Override
    public void valid(DataStandardDTO dataStandardDTO) {
        if (StringUtils.isNotEmpty(dataStandardDTO.getLengthType())
                && StringUtils.isEmpty(dataStandardDTO.getDataLength())) {
            throw new CommonException(ErrorCode.DATA_LENGTH_CAN_NOT_NULL, dataStandardDTO.getStandardCode());
        }
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
