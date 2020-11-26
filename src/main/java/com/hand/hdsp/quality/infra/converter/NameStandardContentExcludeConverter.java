package com.hand.hdsp.quality.infra.converter;

import com.hand.hdsp.quality.domain.entity.NameStandardContentExclude;
import com.hand.hdsp.quality.infra.dataobject.NameStandardContentExcludeDO;
import com.hand.hdsp.quality.api.dto.NameStandardContentExcludeDTO;

import io.choerodon.core.convertor.ConvertorI;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


import java.util.Optional;

/**
 * <p>命名标准落标排除表转换器</p>
 *
 * @author 张鹏 2020-11-26 11:11:24
 */
@Component
public class NameStandardContentExcludeConverter implements ConvertorI<NameStandardContentExclude, NameStandardContentExcludeDO, NameStandardContentExcludeDTO> {

    @Override
    public NameStandardContentExclude dtoToEntity( NameStandardContentExcludeDTO dto) {
        return Optional.ofNullable(dto)
                .map(o -> {
                    NameStandardContentExclude entity = NameStandardContentExclude.builder().build();
                    BeanUtils.copyProperties(dto, entity);
                    return entity;
                }).orElseThrow(IllegalArgumentException::new);
    }

    @Override
    public NameStandardContentExcludeDTO entityToDto(NameStandardContentExclude entity) {

        return Optional.ofNullable(entity)
                .map(o -> {
                    NameStandardContentExcludeDTO dto= NameStandardContentExcludeDTO.builder().build();
                    BeanUtils.copyProperties(entity,dto);
                    return dto;
                }).orElseThrow(IllegalArgumentException::new);
    }
}