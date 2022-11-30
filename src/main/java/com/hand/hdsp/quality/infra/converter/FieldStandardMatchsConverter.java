package com.hand.hdsp.quality.infra.converter;

import com.hand.hdsp.quality.domain.entity.FieldStandardMatchs;
import com.hand.hdsp.quality.infra.dataobject.FieldStandardMatchsDO;
import com.hand.hdsp.quality.api.dto.FieldStandardMatchsDTO;

import io.choerodon.core.convertor.ConvertorI;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


import java.util.Optional;

/**
 * <p>字段标准匹配记录表转换器</p>
 *
 * @author SHIJIE.GAO@HAND-CHINA.COM 2022-11-22 17:55:57
 */
@Component
public class FieldStandardMatchsConverter implements ConvertorI<FieldStandardMatchs, FieldStandardMatchsDO, FieldStandardMatchsDTO> {

    @Override
    public FieldStandardMatchs dtoToEntity( FieldStandardMatchsDTO dto) {
        return Optional.ofNullable(dto)
                .map(o -> {
                    FieldStandardMatchs entity = FieldStandardMatchs.builder().build();
                    BeanUtils.copyProperties(dto, entity);
                    return entity;
                }).orElseThrow(IllegalArgumentException::new);
    }

    @Override
    public FieldStandardMatchsDTO entityToDto(FieldStandardMatchs entity) {

        return Optional.ofNullable(entity)
                .map(o -> {
                    FieldStandardMatchsDTO dto= FieldStandardMatchsDTO.builder().build();
                    BeanUtils.copyProperties(entity,dto);
                    return dto;
                }).orElseThrow(IllegalArgumentException::new);
    }
}