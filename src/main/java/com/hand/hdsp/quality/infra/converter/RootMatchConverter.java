package com.hand.hdsp.quality.infra.converter;

import com.hand.hdsp.quality.domain.entity.RootMatch;
import com.hand.hdsp.quality.infra.dataobject.RootMatchDO;
import com.hand.hdsp.quality.api.dto.RootMatchDTO;

import io.choerodon.core.convertor.ConvertorI;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;


import java.util.Optional;

/**
 * <p>字段标准匹配表转换器</p>
 *
 * @author shijie.gao@hand-china.com 2022-12-07 10:42:30
 */
@Component
public class RootMatchConverter implements ConvertorI<RootMatch, RootMatchDO, RootMatchDTO> {

    @Override
    public RootMatch dtoToEntity( RootMatchDTO dto) {
        return Optional.ofNullable(dto)
                .map(o -> {
                    RootMatch entity = RootMatch.builder().build();
                    BeanUtils.copyProperties(dto, entity);
                    return entity;
                }).orElseThrow(IllegalArgumentException::new);
    }

    @Override
    public RootMatchDTO entityToDto(RootMatch entity) {

        return Optional.ofNullable(entity)
                .map(o -> {
                    RootMatchDTO dto= RootMatchDTO.builder().build();
                    BeanUtils.copyProperties(entity,dto);
                    return dto;
                }).orElseThrow(IllegalArgumentException::new);
    }
}