package com.hand.hdsp.quality.infra.converter;

import com.hand.hdsp.quality.domain.entity.NameAimInclude;
import com.hand.hdsp.quality.infra.dataobject.NameAimIncludeDO;
import com.hand.hdsp.quality.api.dto.NameAimIncludeDTO;

import io.choerodon.core.convertor.ConvertorI;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


import java.util.Optional;

/**
 * <p>落标包含表转换器</p>
 *
 * @author 张鹏 2020-11-27 16:35:40
 */
@Component
public class NameAimIncludeConverter implements ConvertorI<NameAimInclude, NameAimIncludeDO, NameAimIncludeDTO> {

    @Override
    public NameAimInclude dtoToEntity( NameAimIncludeDTO dto) {
        return Optional.ofNullable(dto)
                .map(o -> {
                    NameAimInclude entity = NameAimInclude.builder().build();
                    BeanUtils.copyProperties(dto, entity);
                    return entity;
                }).orElseThrow(IllegalArgumentException::new);
    }

    @Override
    public NameAimIncludeDTO entityToDto(NameAimInclude entity) {

        return Optional.ofNullable(entity)
                .map(o -> {
                    NameAimIncludeDTO dto= NameAimIncludeDTO.builder().build();
                    BeanUtils.copyProperties(entity,dto);
                    return dto;
                }).orElseThrow(IllegalArgumentException::new);
    }
}