package com.hand.hdsp.quality.infra.converter;

import com.hand.hdsp.quality.domain.entity.RootVersion;
import com.hand.hdsp.quality.infra.dataobject.RootVersionDO;
import com.hand.hdsp.quality.api.dto.RootVersionDTO;

import io.choerodon.core.convertor.ConvertorI;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


import java.util.Optional;

/**
 * <p>词根版本转换器</p>
 *
 * @author xin.sheng01@china-hand.com 2022-11-24 11:48:12
 */
@Component
public class RootVersionConverter implements ConvertorI<RootVersion, RootVersionDO, RootVersionDTO> {

    @Override
    public RootVersion dtoToEntity( RootVersionDTO dto) {
        return Optional.ofNullable(dto)
                .map(o -> {
                    RootVersion entity = RootVersion.builder().build();
                    BeanUtils.copyProperties(dto, entity);
                    return entity;
                }).orElseThrow(IllegalArgumentException::new);
    }

    @Override
    public RootVersionDTO entityToDto(RootVersion entity) {

        return Optional.ofNullable(entity)
                .map(o -> {
                    RootVersionDTO dto= RootVersionDTO.builder().build();
                    BeanUtils.copyProperties(entity,dto);
                    return dto;
                }).orElseThrow(IllegalArgumentException::new);
    }
}