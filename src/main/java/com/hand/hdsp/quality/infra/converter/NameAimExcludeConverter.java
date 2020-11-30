package com.hand.hdsp.quality.infra.converter;

import com.hand.hdsp.quality.domain.entity.NameAimExclude;
import com.hand.hdsp.quality.infra.dataobject.NameAimExcludeDO;
import com.hand.hdsp.quality.api.dto.NameAimExcludeDTO;

import io.choerodon.core.convertor.ConvertorI;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


import java.util.Optional;

/**
 * <p>落标排除表转换器</p>
 *
 * @author 张鹏 2020-11-27 16:35:40
 */
@Component
public class NameAimExcludeConverter implements ConvertorI<NameAimExclude, NameAimExcludeDO, NameAimExcludeDTO> {

    @Override
    public NameAimExclude dtoToEntity( NameAimExcludeDTO dto) {
        return Optional.ofNullable(dto)
                .map(o -> {
                    NameAimExclude entity = NameAimExclude.builder().build();
                    BeanUtils.copyProperties(dto, entity);
                    return entity;
                }).orElseThrow(IllegalArgumentException::new);
    }

    @Override
    public NameAimExcludeDTO entityToDto(NameAimExclude entity) {

        return Optional.ofNullable(entity)
                .map(o -> {
                    NameAimExcludeDTO dto= NameAimExcludeDTO.builder().build();
                    BeanUtils.copyProperties(entity,dto);
                    return dto;
                }).orElseThrow(IllegalArgumentException::new);
    }
}