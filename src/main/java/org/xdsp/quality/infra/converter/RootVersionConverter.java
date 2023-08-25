package org.xdsp.quality.infra.converter;

import io.choerodon.core.convertor.ConvertorI;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;
import org.xdsp.quality.api.dto.RootVersionDTO;
import org.xdsp.quality.domain.entity.RootVersion;
import org.xdsp.quality.infra.dataobject.RootVersionDO;

import java.util.Optional;

/**
 * <p>词根版本转换器</p>
 *
 * @author xin.sheng01@china-hand.com 2022-11-24 11:48:12
 */
@Component
public class RootVersionConverter implements ConvertorI<RootVersion, RootVersionDO, RootVersionDTO> {

    @Override
    public RootVersion dtoToEntity( RootVersionDTO dto) {
        return Optional.ofNullable(dto)
                .map(o -> {
                    RootVersion entity = RootVersion.builder().build();
                    BeanUtils.copyProperties(dto, entity);
                    return entity;
                }).orElseThrow(IllegalArgumentException::new);
    }

    @Override
    public RootVersionDTO entityToDto(RootVersion entity) {

        return Optional.ofNullable(entity)
                .map(o -> {
                    RootVersionDTO dto= RootVersionDTO.builder().build();
                    BeanUtils.copyProperties(entity,dto);
                    return dto;
                }).orElseThrow(IllegalArgumentException::new);
    }
}