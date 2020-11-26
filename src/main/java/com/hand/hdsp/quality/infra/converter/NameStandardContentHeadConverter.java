package com.hand.hdsp.quality.infra.converter;

import com.hand.hdsp.quality.domain.entity.NameStandardContentHead;
import com.hand.hdsp.quality.infra.dataobject.NameStandardContentHeadDO;
import com.hand.hdsp.quality.api.dto.NameStandardContentHeadDTO;

import io.choerodon.core.convertor.ConvertorI;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


import java.util.Optional;

/**
 * <p>命名标准落标头表转换器</p>
 *
 * @author 张鹏 2020-11-26 11:11:24
 */
@Component
public class NameStandardContentHeadConverter implements ConvertorI<NameStandardContentHead, NameStandardContentHeadDO, NameStandardContentHeadDTO> {

    @Override
    public NameStandardContentHead dtoToEntity( NameStandardContentHeadDTO dto) {
        return Optional.ofNullable(dto)
                .map(o -> {
                    NameStandardContentHead entity = NameStandardContentHead.builder().build();
                    BeanUtils.copyProperties(dto, entity);
                    return entity;
                }).orElseThrow(IllegalArgumentException::new);
    }

    @Override
    public NameStandardContentHeadDTO entityToDto(NameStandardContentHead entity) {

        return Optional.ofNullable(entity)
                .map(o -> {
                    NameStandardContentHeadDTO dto= NameStandardContentHeadDTO.builder().build();
                    BeanUtils.copyProperties(entity,dto);
                    return dto;
                }).orElseThrow(IllegalArgumentException::new);
    }
}