package org.xdsp.quality.infra.converter;

import io.choerodon.core.convertor.ConvertorI;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;
import org.xdsp.quality.api.dto.RootDTO;
import org.xdsp.quality.domain.entity.Root;
import org.xdsp.quality.infra.dataobject.RootDO;

import java.util.Optional;

/**
 * <p>词根转换器</p>
 *
 * @author xin.sheng01@china-hand.com 2022-11-24 11:48:12
 */
@Component
public class RootConverter implements ConvertorI<Root, RootDO, RootDTO> {

    @Override
    public Root dtoToEntity( RootDTO dto) {
        return Optional.ofNullable(dto)
                .map(o -> {
                    Root entity = Root.builder().build();
                    BeanUtils.copyProperties(dto, entity);
                    return entity;
                }).orElseThrow(IllegalArgumentException::new);
    }

    @Override
    public RootDTO entityToDto(Root entity) {

        return Optional.ofNullable(entity)
                .map(o -> {
                    RootDTO dto= RootDTO.builder().build();
                    BeanUtils.copyProperties(entity,dto);
                    return dto;
                }).orElseThrow(IllegalArgumentException::new);
    }
}