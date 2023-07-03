package org.xdsp.quality.infra.converter;

import io.choerodon.core.convertor.ConvertorI;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;
import org.xdsp.quality.api.dto.BatchPlanDTO;
import org.xdsp.quality.domain.entity.BatchPlan;
import org.xdsp.quality.infra.dataobject.BatchPlanDO;

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
