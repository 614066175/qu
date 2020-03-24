package com.hand.hdsp.quality.infra.converter;

import com.hand.hdsp.quality.api.dto.StreamingResultBaseDTO;
import com.hand.hdsp.quality.domain.entity.StreamingResultBase;
import com.hand.hdsp.quality.infra.dataobject.StreamingResultBaseDO;
import io.choerodon.core.convertor.ConvertorI;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * <p>实时数据方案结果表-基础信息转换器</p>
 *
 * @author feng.liu01@hand-china.com 2020-03-24 16:19:50
 */
@Component
public class StreamingResultBaseConverter implements ConvertorI<StreamingResultBase, StreamingResultBaseDO, StreamingResultBaseDTO> {

    @Override
    public StreamingResultBase dtoToEntity(StreamingResultBaseDTO dto) {
        return Optional.ofNullable(dto)
                .map(o -> {
                    StreamingResultBase entity = StreamingResultBase.builder().build();
                    BeanUtils.copyProperties(dto, entity);
                    return entity;
                }).orElseThrow(IllegalArgumentException::new);
    }

    @Override
    public StreamingResultBaseDTO entityToDto(StreamingResultBase entity) {

        return Optional.ofNullable(entity)
                .map(o -> {
                    StreamingResultBaseDTO dto = StreamingResultBaseDTO.builder().build();
                    BeanUtils.copyProperties(entity, dto);
                    return dto;
                }).orElseThrow(IllegalArgumentException::new);
    }
}
