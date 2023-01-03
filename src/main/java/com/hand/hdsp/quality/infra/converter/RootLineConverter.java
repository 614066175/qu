package com.hand.hdsp.quality.infra.converter;

import com.hand.hdsp.quality.domain.entity.RootLine;
import com.hand.hdsp.quality.infra.dataobject.RootLineDO;
import com.hand.hdsp.quality.api.dto.RootLineDTO;

import io.choerodon.core.convertor.ConvertorI;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


import java.util.Optional;

/**
 * <p>词根中文名行表转换器</p>
 *
 * @author xin.sheng01@china-hand.com 2022-11-24 11:48:12
 */
@Component
public class RootLineConverter implements ConvertorI<RootLine, RootLineDO, RootLineDTO> {

    @Override
    public RootLine dtoToEntity( RootLineDTO dto) {
        return Optional.ofNullable(dto)
                .map(o -> {
                    RootLine entity = RootLine.builder().build();
                    BeanUtils.copyProperties(dto, entity);
                    return entity;
                }).orElseThrow(IllegalArgumentException::new);
    }

    @Override
    public RootLineDTO entityToDto(RootLine entity) {

        return Optional.ofNullable(entity)
                .map(o -> {
                    RootLineDTO dto= RootLineDTO.builder().build();
                    BeanUtils.copyProperties(entity,dto);
                    return dto;
                }).orElseThrow(IllegalArgumentException::new);
    }
}