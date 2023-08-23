package org.xdsp.quality.infra.converter;

import io.choerodon.core.convertor.ConvertorI;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;
import org.xdsp.quality.api.dto.LocVersionDTO;
import org.xdsp.quality.domain.entity.LocVersion;
import org.xdsp.quality.infra.dataobject.LocVersionDO;

import java.util.Optional;

/**
 * <p>loc表转换器</p>
 *
 * @author ZHENG.LI04@HAND-CHINA.COM 2022-04-18 17:20:29
 */
@Component
public class LocVersionConverter implements ConvertorI<LocVersion, LocVersionDO, LocVersionDTO> {

    @Override
    public LocVersion dtoToEntity(LocVersionDTO dto) {
        return Optional.ofNullable(dto)
                .map(o -> {
                    LocVersion entity = LocVersion.builder().build();
                    BeanUtils.copyProperties(dto, entity);
                    return entity;
                }).orElseThrow(IllegalArgumentException::new);
    }

    @Override
    public LocVersionDTO entityToDto(LocVersion entity) {

        return Optional.ofNullable(entity)
                .map(o -> {
                    LocVersionDTO dto = LocVersionDTO.builder().build();
                    BeanUtils.copyProperties(entity, dto);
                    return dto;
                }).orElseThrow(IllegalArgumentException::new);
    }
}