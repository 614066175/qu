package org.xdsp.quality.infra.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.hzero.boot.platform.lov.adapter.LovAdapter;
import org.hzero.boot.platform.lov.dto.LovDTO;
import org.springframework.stereotype.Component;
import org.xdsp.quality.domain.entity.ReferenceData;
import org.xdsp.quality.domain.repository.ReferenceDataRepository;
import org.xdsp.quality.infra.constant.PlanConstant;
import org.xdsp.quality.infra.vo.ValueRangeVo;

import java.util.Objects;

/**
 * 值翻译工具
 *
 * @author xiaoyu.tang@hand-china.com 2024/01/19 16:52
 */
@Component
@Slf4j
public class DataTranslateUtil {

    //===============================================================================
    //  bean注入
    //===============================================================================
    private final ReferenceDataRepository referenceDataRepository;
    private final LovAdapter lovAdapter;

    public DataTranslateUtil(ReferenceDataRepository referenceDataRepository, LovAdapter lovAdapter) {
        this.referenceDataRepository = referenceDataRepository;
        this.lovAdapter = lovAdapter;
    }

    /**
     * 翻译值域类型
     *
     * @param valueType 值域类型
     * @param valueRange 值域范围
     * @param tenantId 租户Id
     */
    public ValueRangeVo translateValueRange(String valueType, String valueRange, Long tenantId) {

        ValueRangeVo valueRangeVo = ValueRangeVo.builder().build();

        if (StringUtils.isNotEmpty(valueType) && StringUtils.isNotEmpty(valueRange)) {
            switch (valueType) {
                case PlanConstant.StandardValueType.VALUE_SET:
                case PlanConstant.StandardValueType.LOV_VIEW: {
                    valueRangeVo.setCode(valueRange);
                    // 翻译值集名称
                    LovDTO lovDTO = lovAdapter.queryLovInfo(valueRange, tenantId);
                    if (Objects.nonNull(lovDTO)) {
                        valueRangeVo.setName(lovDTO.getLovName());
                    }
                }
                break;
                case PlanConstant.StandardValueType.REFERENCE_DATA: {
                    // 翻译参考数据的值
                    Long dataId = Long.parseLong(valueRange);
                    ReferenceData referenceData = referenceDataRepository.selectOne(ReferenceData.builder()
                            .dataId(dataId)
                            .tenantId(tenantId).build());
                    if (Objects.nonNull(referenceData)) {
                        valueRangeVo.setCode(referenceData.getDataCode());
                        valueRangeVo.setName(referenceData.getDataName());
                    }
                }
                break;
                default: {
                    // 不处理
                }
            }
        }
        return valueRangeVo;
    }

}
