package com.hand.hdsp.quality.infra.util;

import com.hand.hdsp.quality.api.dto.*;
import com.hand.hdsp.quality.domain.repository.ReferenceDataRepository;
import com.hand.hdsp.quality.infra.constant.ErrorCode;
import com.hand.hdsp.quality.infra.constant.PlanConstant;
import com.hand.hdsp.quality.infra.constant.WarningLevel;
import io.choerodon.core.exception.CommonException;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.util.Strings;
import org.hzero.starter.driver.core.infra.util.JsonUtil;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import static com.hand.hdsp.quality.infra.constant.PlanConstant.CompareWay.RANGE;

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

    private final ReferenceDataRepository referenceDataRepository;

    public ValueRangeHandler(ReferenceDataRepository referenceDataRepository) {
        this.referenceDataRepository = referenceDataRepository;
    }


    @Override
    public BatchPlanFieldLineDTO handle(DataStandardDTO dataStandardDTO) {
        if (Strings.isEmpty(dataStandardDTO.getValueRange()) || StringUtils.isEmpty(dataStandardDTO.getValueType())) {
            return null;
        }
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
                    if (Objects.isNull(firstWarningLevelDTO)) {
                        warningLevelDTOList = Collections.singletonList(secondWarningLevelDTO);
                    } else {
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
                        .lovCode(dataStandardDTO.getValueRange())
                        .build();
                warningLevelDTOList = Collections.singletonList(warningLevelDTO);
                warningLevel = JsonUtil.toJson(warningLevelDTOList);
                break;
            case PlanConstant.StandardValueType.REFERENCE_DATA:
                String valueRange = dataStandardDTO.getValueRange();
                if (StringUtils.isNotBlank(valueRange)) {
                    long referenceDataId = Long.parseLong(valueRange);
                    ReferenceDataDTO referenceDataDTO = referenceDataRepository.selectDTOByPrimaryKey(referenceDataId);
                    if (Objects.isNull(referenceDataDTO)) {
                        throw new CommonException("Reference data does not exist!");
                    }
                    batchPlanFieldLineDTO.setCountType(PlanConstant.CountType.REFERENCE_DATA);
                    warningLevelDTO = WarningLevelDTO.builder()
                            .warningLevel(WarningLevel.ORANGE)
                            .compareSymbol(PlanConstant.CompareSymbol.INCLUDED)
                            .referenceDataId(referenceDataId)
                            .referenceDataCode(referenceDataDTO.getDataCode())
                            .referenceDataName(referenceDataDTO.getDataName())
                            .build();
                    warningLevelDTOList = Collections.singletonList(warningLevelDTO);
                    warningLevel = JsonUtil.toJson(warningLevelDTOList);
                }
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

    @Override
    public BatchPlanFieldLineDTO handle(DataFieldDTO dataFieldDTO, String fieldType) {
        if (Strings.isEmpty(dataFieldDTO.getValueRange()) || StringUtils.isEmpty(dataFieldDTO.getValueType())) {
            return null;
        }
        BatchPlanFieldLineDTO batchPlanFieldLineDTO = BatchPlanFieldLineDTO.builder()
                .checkWay(PlanConstant.CheckWay.COMMON)
                .checkItem(PlanConstant.CheckItem.FIELD_VALUE)
                .fieldName(dataFieldDTO.getFieldName() + '(' + fieldType + ')')
                .build();
        //判断类型，并生成不同的配置项告警规则
        String warningLevel = "";
        WarningLevelDTO warningLevelDTO;
        List<WarningLevelDTO> warningLevelDTOList = null;
        if (StringUtils.isNotEmpty(dataFieldDTO.getValueType())) {
            switch (dataFieldDTO.getValueType()) {
                case PlanConstant.StandardValueType.AREA:
                    batchPlanFieldLineDTO.setCountType(PlanConstant.CountType.FIXED_VALUE);
                    List<String> valueRangeList = Arrays.asList(dataFieldDTO.getValueRange().split(","));
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
                        if (Objects.isNull(firstWarningLevelDTO)) {
                            warningLevelDTOList = Collections.singletonList(secondWarningLevelDTO);
                        } else {
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
                            .enumValue(dataFieldDTO.getValueRange())
                            .build();
                    warningLevelDTOList = Collections.singletonList(warningLevelDTO);
                    warningLevel = JsonUtil.toJson(warningLevelDTOList);
                    break;
                case PlanConstant.StandardValueType.VALUE_SET:
                    batchPlanFieldLineDTO.setCountType(PlanConstant.CountType.LOV_VALUE);
                    warningLevelDTO = WarningLevelDTO.builder()
                            .warningLevel(WarningLevel.ORANGE)
                            .compareSymbol(PlanConstant.CompareSymbol.INCLUDED)
                            .lovCode(dataFieldDTO.getValueRange())
                            .build();
                    warningLevelDTOList = Collections.singletonList(warningLevelDTO);
                    warningLevel = JsonUtil.toJson(warningLevelDTOList);
                    break;
                case PlanConstant.StandardValueType.REFERENCE_DATA:
                    // 参考数据生成标准逻辑
                    String valueRange = dataFieldDTO.getValueRange();
                    if (StringUtils.isNotBlank(valueRange)) {
                        long referenceDataId = Long.parseLong(valueRange);
                        ReferenceDataDTO referenceDataDTO = referenceDataRepository.selectDTOByPrimaryKey(referenceDataId);
                        if (Objects.isNull(referenceDataDTO)) {
                            throw new CommonException("Reference data does not exist!");
                        }
                        batchPlanFieldLineDTO.setCountType(PlanConstant.CountType.REFERENCE_DATA);
                        warningLevelDTO = WarningLevelDTO.builder()
                                .warningLevel(WarningLevel.ORANGE)
                                .compareSymbol(PlanConstant.CompareSymbol.INCLUDED)
                                .referenceDataId(referenceDataId)
                                .referenceDataCode(referenceDataDTO.getDataCode())
                                .referenceDataName(referenceDataDTO.getDataName())
                                .build();
                        warningLevelDTOList = Collections.singletonList(warningLevelDTO);
                        warningLevel = JsonUtil.toJson(warningLevelDTOList);
                    }
                    break;
                default:
                    break;
            }
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

    @Override
    public void valid(DataStandardDTO dataStandardDTO) {
        if (StringUtils.isNotEmpty(dataStandardDTO.getValueType())
                && StringUtils.isEmpty(dataStandardDTO.getValueRange())) {
            throw new CommonException(ErrorCode.VALUE_RANGE_CAN_NOT_NULL);
        }
    }
}
