package com.hand.hdsp.quality.infra.converter;

import com.hand.hdsp.quality.api.dto.LovVersionDTO;
import com.hand.hdsp.quality.domain.entity.LovVersion;
import com.hand.hdsp.quality.infra.dataobject.LovVersionDO;
import io.choerodon.core.convertor.ConvertorI;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * <p>LOV表转换器</p>
 *
 * @author ZHENG.LI04@HAND-CHINA.COM 2022-04-18 17:20:29
 */
@Component
public class LovVersionConverter implements ConvertorI<LovVersion, LovVersionDO, LovVersionDTO> {

    @Override
    public LovVersion dtoToEntity( LovVersionDTO dto) {
        return Optional.ofNullable(dto)
                .map(o -> {
                    LovVersion entity = LovVersion.builder().build();
                    BeanUtils.copyProperties(dto, entity);
                    return entity;
                }).orElseThrow(IllegalArgumentException::new);
    }

    @Override
    public LovVersionDTO entityToDto(LovVersion entity) {

        return Optional.ofNullable(entity)
                .map(o -> {
                    LovVersionDTO dto= LovVersionDTO.builder().build();
                    BeanUtils.copyProperties(entity,dto);
                    return dto;
                }).orElseThrow(IllegalArgumentException::new);
    }
}