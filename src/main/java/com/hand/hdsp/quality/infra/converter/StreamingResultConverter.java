package com.hand.hdsp.quality.infra.converter;

import com.hand.hdsp.quality.api.dto.StreamingResultDTO;
import com.hand.hdsp.quality.domain.entity.StreamingResult;
import com.hand.hdsp.quality.infra.dataobject.StreamingResultDO;
import io.choerodon.core.convertor.ConvertorI;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * <p>实时数据方案结果表转换器</p>
 *
 * @author feng.liu01@hand-china.com 2020-03-24 16:19:50
 */
@Component
public class StreamingResultConverter implements ConvertorI<StreamingResult, StreamingResultDO, StreamingResultDTO> {

    @Override
    public StreamingResult dtoToEntity(StreamingResultDTO dto) {
        return Optional.ofNullable(dto)
                .map(o -> {
                    StreamingResult entity = StreamingResult.builder().build();
                    BeanUtils.copyProperties(dto, entity);
                    return entity;
                }).orElseThrow(IllegalArgumentException::new);
    }

    @Override
    public StreamingResultDTO entityToDto(StreamingResult entity) {

        return Optional.ofNullable(entity)
                .map(o -> {
                    StreamingResultDTO dto = StreamingResultDTO.builder().build();
                    BeanUtils.copyProperties(entity, dto);
                    return dto;
                }).orElseThrow(IllegalArgumentException::new);
    }
}
