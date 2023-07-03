package org.xdsp.quality.infra.converter;

import io.choerodon.core.convertor.ConvertorI;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;
import org.xdsp.quality.api.dto.StandardOutbibDTO;
import org.xdsp.quality.domain.entity.StandardOutbib;
import org.xdsp.quality.infra.dataobject.StandardOutbibDO;

import java.util.Optional;

/**
 * <p>标准落标表转换器</p>
 *
 * @author guoliangli01.@hand-china.com 2020-11-25 17:03:11
 */
@Component
public class StandardOutbibConverter implements ConvertorI<StandardOutbib, StandardOutbibDO, StandardOutbibDTO> {

    @Override
    public StandardOutbib dtoToEntity(StandardOutbibDTO dto) {
        return Optional.ofNullable(dto)
                .map(o -> {
                    StandardOutbib entity = StandardOutbib.builder().build();
                    BeanUtils.copyProperties(dto, entity);
                    return entity;
                }).orElseThrow(IllegalArgumentException::new);
    }

    @Override
    public StandardOutbibDTO entityToDto(StandardOutbib entity) {

        return Optional.ofNullable(entity)
                .map(o -> {
                    StandardOutbibDTO dto= StandardOutbibDTO.builder().build();
                    BeanUtils.copyProperties(entity,dto);
                    return dto;
                }).orElseThrow(IllegalArgumentException::new);
    }
}