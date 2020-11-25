package com.hand.hdsp.quality.infra.converter;

import java.util.Optional;

import com.hand.hdsp.quality.api.dto.StandardExtraDTO;
import com.hand.hdsp.quality.domain.entity.StandardExtra;
import com.hand.hdsp.quality.infra.dataobject.StandardExtraDO;
import io.choerodon.core.convertor.ConvertorI;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

/**
 * <p>标准额外信息表转换器</p>
 *
 * @author guoliangli01.@hand-china.com 2020-11-25 17:03:11
 */
@Component
public class StandardExtraConverter implements ConvertorI<StandardExtra, StandardExtraDO, StandardExtraDTO> {

    @Override
    public StandardExtra dtoToEntity(StandardExtraDTO dto) {
        return Optional.ofNullable(dto)
                .map(o -> {
                    StandardExtra entity = StandardExtra.builder().build();
                    BeanUtils.copyProperties(dto, entity);
                    return entity;
                }).orElseThrow(IllegalArgumentException::new);
    }

    @Override
    public StandardExtraDTO entityToDto(StandardExtra entity) {

        return Optional.ofNullable(entity)
                .map(o -> {
                    StandardExtraDTO dto= StandardExtraDTO.builder().build();
                    BeanUtils.copyProperties(entity,dto);
                    return dto;
                }).orElseThrow(IllegalArgumentException::new);
    }
}