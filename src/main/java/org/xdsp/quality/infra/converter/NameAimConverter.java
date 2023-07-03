package org.xdsp.quality.infra.converter;

import io.choerodon.core.convertor.ConvertorI;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;
import org.xdsp.quality.api.dto.NameAimDTO;
import org.xdsp.quality.domain.entity.NameAim;
import org.xdsp.quality.infra.dataobject.NameAimDO;

import java.util.Optional;

/**
 * <p>命名落标表转换器</p>
 *
 * @author 张鹏 2020-11-27 16:35:40
 */
@Component
public class NameAimConverter implements ConvertorI<NameAim, NameAimDO, NameAimDTO> {

    @Override
    public NameAim dtoToEntity( NameAimDTO dto) {
        return Optional.ofNullable(dto)
                .map(o -> {
                    NameAim entity = NameAim.builder().build();
                    BeanUtils.copyProperties(dto, entity);
                    return entity;
                }).orElseThrow(IllegalArgumentException::new);
    }

    @Override
    public NameAimDTO entityToDto(NameAim entity) {

        return Optional.ofNullable(entity)
                .map(o -> {
                    NameAimDTO dto= NameAimDTO.builder().build();
                    BeanUtils.copyProperties(entity,dto);
                    return dto;
                }).orElseThrow(IllegalArgumentException::new);
    }
}