package org.xdsp.quality.infra.converter;

import io.choerodon.core.convertor.ConvertorI;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;
import org.xdsp.quality.api.dto.StandardExtraDTO;
import org.xdsp.quality.domain.entity.StandardExtra;
import org.xdsp.quality.infra.dataobject.StandardExtraDO;

import java.util.Optional;

/**
 * <p>标准附加信息表转换器</p>
 *
 * @author guoliangli01@hand-china.com 2020-11-27 14:36:45
 */
@Component
public class StandardExtraConverter implements ConvertorI<StandardExtra, StandardExtraDO, StandardExtraDTO> {

    @Override
    public StandardExtra dtoToEntity(StandardExtraDTO dto) {
        return Optional.ofNullable(dto)
                .map(o -> {
                    StandardExtra entity = StandardExtra.builder().build();
                    BeanUtils.copyProperties(dto, entity);
                    return entity;
                }).orElseThrow(IllegalArgumentException::new);
    }

    @Override
    public StandardExtraDTO entityToDto(StandardExtra entity) {

        return Optional.ofNullable(entity)
                .map(o -> {
                    StandardExtraDTO dto= StandardExtraDTO.builder().build();
                    BeanUtils.copyProperties(entity,dto);
                    return dto;
                }).orElseThrow(IllegalArgumentException::new);
    }
}