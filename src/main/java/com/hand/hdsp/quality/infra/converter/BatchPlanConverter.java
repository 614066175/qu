package com.hand.hdsp.quality.infra.converter;

import com.hand.hdsp.quality.api.dto.BatchPlanDTO;
import com.hand.hdsp.quality.domain.entity.BatchPlan;
import com.hand.hdsp.quality.infra.dataobject.BatchPlanDO;
import io.choerodon.core.convertor.ConvertorI;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * <p>批数据评估方案表转换器</p>
 *
 * @author feng.liu01@hand-china.com 2020-03-24 16:19:52
 */
@Component
public class BatchPlanConverter implements ConvertorI<BatchPlan, BatchPlanDO, BatchPlanDTO> {

    @Override
    public BatchPlan dtoToEntity(BatchPlanDTO dto) {
        return Optional.ofNullable(dto)
                .map(o -> {
                    BatchPlan entity = BatchPlan.builder().build();
                    BeanUtils.copyProperties(dto, entity);
                    return entity;
                }).orElseThrow(IllegalArgumentException::new);
    }

    @Override
    public BatchPlanDTO entityToDto(BatchPlan entity) {

        return Optional.ofNullable(entity)
                .map(o -> {
                    BatchPlanDTO dto = BatchPlanDTO.builder().build();
                    BeanUtils.copyProperties(entity, dto);
                    return dto;
                }).orElseThrow(IllegalArgumentException::new);
    }
}
