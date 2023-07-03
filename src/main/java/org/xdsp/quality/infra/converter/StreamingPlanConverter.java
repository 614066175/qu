package org.xdsp.quality.infra.converter;

import io.choerodon.core.convertor.ConvertorI;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;
import org.xdsp.quality.api.dto.StreamingPlanDTO;
import org.xdsp.quality.domain.entity.StreamingPlan;
import org.xdsp.quality.infra.dataobject.StreamingPlanDO;

import java.util.Optional;

/**
 * <p>实时数据评估方案表转换器</p>
 *
 * @author feng.liu01@hand-china.com 2020-03-24 16:19:51
 */
@Component
public class StreamingPlanConverter implements ConvertorI<StreamingPlan, StreamingPlanDO, StreamingPlanDTO> {

    @Override
    public StreamingPlan dtoToEntity(StreamingPlanDTO dto) {
        return Optional.ofNullable(dto)
                .map(o -> {
                    StreamingPlan entity = StreamingPlan.builder().build();
                    BeanUtils.copyProperties(dto, entity);
                    return entity;
                }).orElseThrow(IllegalArgumentException::new);
    }

    @Override
    public StreamingPlanDTO entityToDto(StreamingPlan entity) {

        return Optional.ofNullable(entity)
                .map(o -> {
                    StreamingPlanDTO dto = StreamingPlanDTO.builder().build();
                    BeanUtils.copyProperties(entity, dto);
                    return dto;
                }).orElseThrow(IllegalArgumentException::new);
    }
}
