package com.hand.hdsp.quality.infra.converter;

import com.hand.hdsp.quality.api.dto.LocValueVersionDTO;
import com.hand.hdsp.quality.domain.entity.LocValueVersion;
import com.hand.hdsp.quality.infra.dataobject.LocValueVersionDO;
import io.choerodon.core.convertor.ConvertorI;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * <p>loc独立值集表转换器</p>
 *
 * @author ZHENG.LI04@HAND-CHINA.COM 2022-04-19 16:38:55
 */
@Component
public class LocValueVersionConverter implements ConvertorI<LocValueVersion, LocValueVersionDO, LocValueVersionDTO> {

    @Override
    public LocValueVersion dtoToEntity(LocValueVersionDTO dto) {
        return Optional.ofNullable(dto)
                .map(o -> {
                    LocValueVersion entity = LocValueVersion.builder().build();
                    BeanUtils.copyProperties(dto, entity);
                    return entity;
                }).orElseThrow(IllegalArgumentException::new);
    }

    @Override
    public LocValueVersionDTO entityToDto(LocValueVersion entity) {

        return Optional.ofNullable(entity)
                .map(o -> {
                    LocValueVersionDTO dto = LocValueVersionDTO.builder().build();
                    BeanUtils.copyProperties(entity, dto);
                    return dto;
                }).orElseThrow(IllegalArgumentException::new);
    }
}