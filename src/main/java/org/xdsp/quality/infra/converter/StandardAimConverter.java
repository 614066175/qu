package org.xdsp.quality.infra.converter;

import io.choerodon.core.convertor.ConvertorI;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;
import org.xdsp.quality.api.dto.StandardAimDTO;
import org.xdsp.quality.domain.entity.StandardAim;
import org.xdsp.quality.infra.dataobject.StandardAimDO;

import java.util.Optional;

/**
 * <p>标准落标表转换器</p>
 *
 * @author guoliangli01@hand-china.com 2020-11-30 10:23:51
 */
@Component
public class StandardAimConverter implements ConvertorI<StandardAim, StandardAimDO, StandardAimDTO> {

    @Override
    public StandardAim dtoToEntity(StandardAimDTO dto) {
        return Optional.ofNullable(dto)
                .map(o -> {
                    StandardAim entity = StandardAim.builder().build();
                    BeanUtils.copyProperties(dto, entity);
                    return entity;
                }).orElseThrow(IllegalArgumentException::new);
    }

    @Override
    public StandardAimDTO entityToDto(StandardAim entity) {

        return Optional.ofNullable(entity)
                .map(o -> {
                    StandardAimDTO dto= StandardAimDTO.builder().build();
                    BeanUtils.copyProperties(entity,dto);
                    return dto;
                }).orElseThrow(IllegalArgumentException::new);
    }
}