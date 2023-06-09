package org.xdsp.quality.infra.converter;

import io.choerodon.core.convertor.ConvertorI;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;
import org.xdsp.quality.api.dto.ExtraVersionDTO;
import org.xdsp.quality.domain.entity.ExtraVersion;
import org.xdsp.quality.infra.dataobject.ExtraVersionDO;

import java.util.Optional;

/**
 * <p>附加信息版本表转换器</p>
 *
 * @author guoliangli01@hand-china.com 2020-11-27 14:36:45
 */
@Component
public class ExtraVersionConverter implements ConvertorI<ExtraVersion, ExtraVersionDO, ExtraVersionDTO> {

    @Override
    public ExtraVersion dtoToEntity(ExtraVersionDTO dto) {
        return Optional.ofNullable(dto)
                .map(o -> {
                    ExtraVersion entity = ExtraVersion.builder().build();
                    BeanUtils.copyProperties(dto, entity);
                    return entity;
                }).orElseThrow(IllegalArgumentException::new);
    }

    @Override
    public ExtraVersionDTO entityToDto(ExtraVersion entity) {

        return Optional.ofNullable(entity)
                .map(o -> {
                    ExtraVersionDTO dto= ExtraVersionDTO.builder().build();
                    BeanUtils.copyProperties(entity,dto);
                    return dto;
                }).orElseThrow(IllegalArgumentException::new);
    }
}