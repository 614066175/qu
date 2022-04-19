package com.hand.hdsp.quality.infra.converter;

import com.hand.hdsp.quality.api.dto.LovDTO;
import com.hand.hdsp.quality.domain.entity.Lov;
import com.hand.hdsp.quality.infra.dataobject.LovDO;
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
public class LovConverter implements ConvertorI<Lov, LovDO, LovDTO> {

    @Override
    public Lov dtoToEntity( LovDTO dto) {
        return Optional.ofNullable(dto)
                .map(o -> {
                    Lov entity = Lov.builder().build();
                    BeanUtils.copyProperties(dto, entity);
                    return entity;
                }).orElseThrow(IllegalArgumentException::new);
    }

    @Override
    public LovDTO entityToDto(Lov entity) {

        return Optional.ofNullable(entity)
                .map(o -> {
                    LovDTO dto= LovDTO.builder().build();
                    BeanUtils.copyProperties(entity,dto);
                    return dto;
                }).orElseThrow(IllegalArgumentException::new);
    }
}