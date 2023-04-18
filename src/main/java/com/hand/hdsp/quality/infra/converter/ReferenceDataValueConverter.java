package com.hand.hdsp.quality.infra.converter;

import com.hand.hdsp.quality.domain.entity.ReferenceDataValue;
import com.hand.hdsp.quality.infra.dataobject.ReferenceDataValueDO;
import com.hand.hdsp.quality.api.dto.ReferenceDataValueDTO;

import io.choerodon.core.convertor.ConvertorI;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


import java.util.Optional;

/**
 * <p>参考数据值转换器</p>
 *
 * @author fuqiang.luo@hand-china.com 2023-04-14 16:23:35
 */
@Component
public class ReferenceDataValueConverter implements ConvertorI<ReferenceDataValue, ReferenceDataValueDO, ReferenceDataValueDTO> {

    @Override
    public ReferenceDataValue dtoToEntity( ReferenceDataValueDTO dto) {
        return Optional.ofNullable(dto)
                .map(o -> {
                    ReferenceDataValue entity = ReferenceDataValue.builder().build();
                    BeanUtils.copyProperties(dto, entity);
                    return entity;
                }).orElseThrow(IllegalArgumentException::new);
    }

    @Override
    public ReferenceDataValueDTO entityToDto(ReferenceDataValue entity) {

        return Optional.ofNullable(entity)
                .map(o -> {
                    ReferenceDataValueDTO dto= ReferenceDataValueDTO.builder().build();
                    BeanUtils.copyProperties(entity,dto);
                    return dto;
                }).orElseThrow(IllegalArgumentException::new);
    }
}