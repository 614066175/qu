package org.xdsp.quality.infra.converter;

import io.choerodon.core.convertor.ConvertorI;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;
import org.xdsp.quality.api.dto.LocValueDTO;
import org.xdsp.quality.domain.entity.LocValue;
import org.xdsp.quality.infra.dataobject.LocValueDO;

import java.util.Optional;

/**
 * <p>loc独立值集表转换器</p>
 *
 * @author ZHENG.LI04@HAND-CHINA.COM 2022-04-18 17:20:29
 */
@Component
public class LocValueConverter implements ConvertorI<LocValue, LocValueDO, LocValueDTO> {

    @Override
    public LocValue dtoToEntity(LocValueDTO dto) {
        return Optional.ofNullable(dto)
                .map(o -> {
                    LocValue entity = LocValue.builder().build();
                    BeanUtils.copyProperties(dto, entity);
                    return entity;
                }).orElseThrow(IllegalArgumentException::new);
    }

    @Override
    public LocValueDTO entityToDto(LocValue entity) {

        return Optional.ofNullable(entity)
                .map(o -> {
                    LocValueDTO dto = LocValueDTO.builder().build();
                    BeanUtils.copyProperties(entity, dto);
                    return dto;
                }).orElseThrow(IllegalArgumentException::new);
    }
}