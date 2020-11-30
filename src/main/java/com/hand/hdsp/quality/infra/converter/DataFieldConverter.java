package com.hand.hdsp.quality.infra.converter;

import com.hand.hdsp.quality.domain.entity.DataField;
import com.hand.hdsp.quality.infra.dataobject.DataFieldDO;
import com.hand.hdsp.quality.api.dto.DataFieldDTO;

import io.choerodon.core.convertor.ConvertorI;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


import java.util.Optional;

/**
 * <p>字段标准表转换器</p>
 *
 * @author wsl 2020-11-25 15:38:21
 */
@Component
public class DataFieldConverter implements ConvertorI<DataField, DataFieldDO, DataFieldDTO> {

    @Override
    public DataField dtoToEntity( DataFieldDTO dto) {
        return Optional.ofNullable(dto)
                .map(o -> {
                    DataField entity = DataField.builder().build();
                    BeanUtils.copyProperties(dto, entity);
                    return entity;
                }).orElseThrow(IllegalArgumentException::new);
    }

    @Override
    public DataFieldDTO entityToDto(DataField entity) {

        return Optional.ofNullable(entity)
                .map(o -> {
                    DataFieldDTO dto= DataFieldDTO.builder().build();
                    BeanUtils.copyProperties(entity,dto);
                    return dto;
                }).orElseThrow(IllegalArgumentException::new);
    }
}