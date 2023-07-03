package org.xdsp.quality.infra.converter;

import io.choerodon.core.convertor.ConvertorI;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;
import org.xdsp.quality.api.dto.StandardGroupDTO;
import org.xdsp.quality.domain.entity.StandardGroup;
import org.xdsp.quality.infra.dataobject.StandardGroupDO;

import java.util.Optional;

/**
 * <p>标准分组表转换器</p>
 *
 * @author guoliangli01@hand-china.com 2020-11-27 14:36:44
 */
@Component
public class StandardGroupConverter implements ConvertorI<StandardGroup, StandardGroupDO, StandardGroupDTO> {

    @Override
    public StandardGroup dtoToEntity(StandardGroupDTO dto) {
        return Optional.ofNullable(dto)
                .map(o -> {
                    StandardGroup entity = StandardGroup.builder().build();
                    BeanUtils.copyProperties(dto, entity);
                    return entity;
                }).orElseThrow(IllegalArgumentException::new);
    }

    @Override
    public StandardGroupDTO entityToDto(StandardGroup entity) {

        return Optional.ofNullable(entity)
                .map(o -> {
                    StandardGroupDTO dto= StandardGroupDTO.builder().build();
                    BeanUtils.copyProperties(entity,dto);
                    return dto;
                }).orElseThrow(IllegalArgumentException::new);
    }
}