package com.hand.hdsp.quality.infra.converter;

import com.hand.hdsp.quality.domain.entity.NameStandardContentLine;
import com.hand.hdsp.quality.infra.dataobject.NameStandardContentLineDO;
import com.hand.hdsp.quality.api.dto.NameStandardContentLineDTO;

import io.choerodon.core.convertor.ConvertorI;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


import java.util.Optional;

/**
 * <p>命名标准落标行表转换器</p>
 *
 * @author 张鹏 2020-11-26 11:11:24
 */
@Component
public class NameStandardContentLineConverter implements ConvertorI<NameStandardContentLine, NameStandardContentLineDO, NameStandardContentLineDTO> {

    @Override
    public NameStandardContentLine dtoToEntity( NameStandardContentLineDTO dto) {
        return Optional.ofNullable(dto)
                .map(o -> {
                    NameStandardContentLine entity = NameStandardContentLine.builder().build();
                    BeanUtils.copyProperties(dto, entity);
                    return entity;
                }).orElseThrow(IllegalArgumentException::new);
    }

    @Override
    public NameStandardContentLineDTO entityToDto(NameStandardContentLine entity) {

        return Optional.ofNullable(entity)
                .map(o -> {
                    NameStandardContentLineDTO dto= NameStandardContentLineDTO.builder().build();
                    BeanUtils.copyProperties(entity,dto);
                    return dto;
                }).orElseThrow(IllegalArgumentException::new);
    }
}