package com.hand.hdsp.quality.infra.converter;

import com.hand.hdsp.quality.domain.entity.FieldStandardMatching;
import com.hand.hdsp.quality.infra.dataobject.FieldStandardMatchingDO;
import com.hand.hdsp.quality.api.dto.FieldStandardMatchingDTO;

import io.choerodon.core.convertor.ConvertorI;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


import java.util.Optional;

/**
 * <p>字段标准匹配表转换器</p>
 *
 * @author shijie.gao@hand-china.com 2022-11-30 10:31:02
 */
@Component
public class FieldStandardMatchingConverter implements ConvertorI<FieldStandardMatching, FieldStandardMatchingDO, FieldStandardMatchingDTO> {

    @Override
    public FieldStandardMatching dtoToEntity( FieldStandardMatchingDTO dto) {
        return Optional.ofNullable(dto)
                .map(o -> {
                    FieldStandardMatching entity = FieldStandardMatching.builder().build();
                    BeanUtils.copyProperties(dto, entity);
                    return entity;
                }).orElseThrow(IllegalArgumentException::new);
    }

    @Override
    public FieldStandardMatchingDTO entityToDto(FieldStandardMatching entity) {

        return Optional.ofNullable(entity)
                .map(o -> {
                    FieldStandardMatchingDTO dto= FieldStandardMatchingDTO.builder().build();
                    BeanUtils.copyProperties(entity,dto);
                    return dto;
                }).orElseThrow(IllegalArgumentException::new);
    }
}