package com.hand.hdsp.quality.infra.converter;

import com.hand.hdsp.quality.domain.entity.NameAim;
import com.hand.hdsp.quality.infra.dataobject.NameAimDO;
import com.hand.hdsp.quality.api.dto.NameAimDTO;

import io.choerodon.core.convertor.ConvertorI;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


import java.util.Optional;

/**
 * <p>命名落标表转换器</p>
 *
 * @author 张鹏 2020-11-27 16:35:40
 */
@Component
public class NameAimConverter implements ConvertorI<NameAim, NameAimDO, NameAimDTO> {

    @Override
    public NameAim dtoToEntity( NameAimDTO dto) {
        return Optional.ofNullable(dto)
                .map(o -> {
                    NameAim entity = NameAim.builder().build();
                    BeanUtils.copyProperties(dto, entity);
                    return entity;
                }).orElseThrow(IllegalArgumentException::new);
    }

    @Override
    public NameAimDTO entityToDto(NameAim entity) {

        return Optional.ofNullable(entity)
                .map(o -> {
                    NameAimDTO dto= NameAimDTO.builder().build();
                    BeanUtils.copyProperties(entity,dto);
                    return dto;
                }).orElseThrow(IllegalArgumentException::new);
    }
}