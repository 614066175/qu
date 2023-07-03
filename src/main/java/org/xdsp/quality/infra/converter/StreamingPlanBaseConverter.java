package org.xdsp.quality.infra.converter;

import io.choerodon.core.convertor.ConvertorI;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;
import org.xdsp.quality.api.dto.StreamingPlanBaseDTO;
import org.xdsp.quality.domain.entity.StreamingPlanBase;
import org.xdsp.quality.infra.dataobject.StreamingPlanBaseDO;

import java.util.Optional;

/**
 * <p>实时数据方案-基础配置表转换器</p>
 *
 * @author feng.liu01@hand-china.com 2020-03-24 16:19:51
 */
@Component
public class StreamingPlanBaseConverter implements ConvertorI<StreamingPlanBase, StreamingPlanBaseDO, StreamingPlanBaseDTO> {

    @Override
    public StreamingPlanBase dtoToEntity(StreamingPlanBaseDTO dto) {
        return Optional.ofNullable(dto)
                .map(o -> {
                    StreamingPlanBase entity = StreamingPlanBase.builder().build();
                    BeanUtils.copyProperties(dto, entity);
                    return entity;
                }).orElseThrow(IllegalArgumentException::new);
    }

    @Override
    public StreamingPlanBaseDTO entityToDto(StreamingPlanBase entity) {

        return Optional.ofNullable(entity)
                .map(o -> {
                    StreamingPlanBaseDTO dto = StreamingPlanBaseDTO.builder().build();
                    BeanUtils.copyProperties(entity, dto);
                    return dto;
                }).orElseThrow(IllegalArgumentException::new);
    }
}
