package com.hand.hdsp.quality.infra.converter;

import com.hand.hdsp.quality.api.dto.LovValueDTO;
import com.hand.hdsp.quality.domain.entity.LovValue;
import com.hand.hdsp.quality.infra.dataobject.LovValueDO;
import io.choerodon.core.convertor.ConvertorI;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * <p>LOV独立值集表转换器</p>
 *
 * @author ZHENG.LI04@HAND-CHINA.COM 2022-04-18 17:20:29
 */
@Component
public class LovValueConverter implements ConvertorI<LovValue, LovValueDO, LovValueDTO> {

    @Override
    public LovValue dtoToEntity( LovValueDTO dto) {
        return Optional.ofNullable(dto)
                .map(o -> {
                    LovValue entity = LovValue.builder().build();
                    BeanUtils.copyProperties(dto, entity);
                    return entity;
                }).orElseThrow(IllegalArgumentException::new);
    }

    @Override
    public LovValueDTO entityToDto(LovValue entity) {

        return Optional.ofNullable(entity)
                .map(o -> {
                    LovValueDTO dto= LovValueDTO.builder().build();
                    BeanUtils.copyProperties(entity,dto);
                    return dto;
                }).orElseThrow(IllegalArgumentException::new);
    }
}