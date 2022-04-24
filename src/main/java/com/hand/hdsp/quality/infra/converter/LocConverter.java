package com.hand.hdsp.quality.infra.converter;

import com.hand.hdsp.quality.api.dto.LocDTO;
import com.hand.hdsp.quality.domain.entity.Loc;
import com.hand.hdsp.quality.infra.dataobject.LocDO;
import io.choerodon.core.convertor.ConvertorI;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * <p>loc表转换器</p>
 *
 * @author ZHENG.LI04@HAND-CHINA.COM 2022-04-18 17:20:29
 */
@Component
public class LocConverter implements ConvertorI<Loc, LocDO, LocDTO> {

    @Override
    public Loc dtoToEntity(LocDTO dto) {
        return Optional.ofNullable(dto)
                .map(o -> {
                    Loc entity = Loc.builder().build();
                    BeanUtils.copyProperties(dto, entity);
                    return entity;
                }).orElseThrow(IllegalArgumentException::new);
    }

    @Override
    public LocDTO entityToDto(Loc entity) {

        return Optional.ofNullable(entity)
                .map(o -> {
                    LocDTO dto = LocDTO.builder().build();
                    BeanUtils.copyProperties(entity, dto);
                    return dto;
                }).orElseThrow(IllegalArgumentException::new);
    }
}