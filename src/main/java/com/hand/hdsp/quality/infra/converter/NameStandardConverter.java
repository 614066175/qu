package com.hand.hdsp.quality.infra.converter;

import com.hand.hdsp.quality.domain.entity.NameStandard;
import com.hand.hdsp.quality.infra.dataobject.NameStandardDO;
import com.hand.hdsp.quality.api.dto.NameStandardDTO;

import io.choerodon.core.convertor.ConvertorI;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


import java.util.Optional;

/**
 * <p>命名标准表转换器</p>
 *
 * @author 张鹏 2020-11-26 11:11:24
 */
@Component
public class NameStandardConverter implements ConvertorI<NameStandard, NameStandardDO, NameStandardDTO> {

    @Override
    public NameStandard dtoToEntity( NameStandardDTO dto) {
        return Optional.ofNullable(dto)
                .map(o -> {
                    NameStandard entity = NameStandard.builder().build();
                    BeanUtils.copyProperties(dto, entity);
                    return entity;
                }).orElseThrow(IllegalArgumentException::new);
    }

    @Override
    public NameStandardDTO entityToDto(NameStandard entity) {

        return Optional.ofNullable(entity)
                .map(o -> {
                    NameStandardDTO dto= NameStandardDTO.builder().build();
                    BeanUtils.copyProperties(entity,dto);
                    return dto;
                }).orElseThrow(IllegalArgumentException::new);
    }
}