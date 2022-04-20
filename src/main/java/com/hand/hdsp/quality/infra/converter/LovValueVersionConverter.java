package com.hand.hdsp.quality.infra.converter;

import com.hand.hdsp.quality.api.dto.LovValueVersionDTO;
import com.hand.hdsp.quality.domain.entity.LovValueVersion;
import com.hand.hdsp.quality.infra.dataobject.LovValueVersionDO;
import io.choerodon.core.convertor.ConvertorI;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * <p>LOV独立值集表转换器</p>
 *
 * @author ZHENG.LI04@HAND-CHINA.COM 2022-04-19 16:38:55
 */
@Component
public class LovValueVersionConverter implements ConvertorI<LovValueVersion, LovValueVersionDO, LovValueVersionDTO> {

    @Override
    public LovValueVersion dtoToEntity( LovValueVersionDTO dto) {
        return Optional.ofNullable(dto)
                .map(o -> {
                    LovValueVersion entity = LovValueVersion.builder().build();
                    BeanUtils.copyProperties(dto, entity);
                    return entity;
                }).orElseThrow(IllegalArgumentException::new);
    }

    @Override
    public LovValueVersionDTO entityToDto(LovValueVersion entity) {

        return Optional.ofNullable(entity)
                .map(o -> {
                    LovValueVersionDTO dto= LovValueVersionDTO.builder().build();
                    BeanUtils.copyProperties(entity,dto);
                    return dto;
                }).orElseThrow(IllegalArgumentException::new);
    }
}