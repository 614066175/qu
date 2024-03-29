package org.xdsp.quality.infra.converter;

import io.choerodon.core.convertor.ConvertorI;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;
import org.xdsp.quality.api.dto.NameAimIncludeDTO;
import org.xdsp.quality.domain.entity.NameAimInclude;
import org.xdsp.quality.infra.dataobject.NameAimIncludeDO;

import java.util.Optional;

/**
 * <p>落标包含表转换器</p>
 *
 * @author 张鹏 2020-11-27 16:35:40
 */
@Component
public class NameAimIncludeConverter implements ConvertorI<NameAimInclude, NameAimIncludeDO, NameAimIncludeDTO> {

    @Override
    public NameAimInclude dtoToEntity( NameAimIncludeDTO dto) {
        return Optional.ofNullable(dto)
                .map(o -> {
                    NameAimInclude entity = NameAimInclude.builder().build();
                    BeanUtils.copyProperties(dto, entity);
                    return entity;
                }).orElseThrow(IllegalArgumentException::new);
    }

    @Override
    public NameAimIncludeDTO entityToDto(NameAimInclude entity) {

        return Optional.ofNullable(entity)
                .map(o -> {
                    NameAimIncludeDTO dto= NameAimIncludeDTO.builder().build();
                    BeanUtils.copyProperties(entity,dto);
                    return dto;
                }).orElseThrow(IllegalArgumentException::new);
    }
}