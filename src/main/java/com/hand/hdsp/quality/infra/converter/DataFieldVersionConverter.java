package com.hand.hdsp.quality.infra.converter;

import com.hand.hdsp.quality.domain.entity.DataFieldVersion;
import com.hand.hdsp.quality.infra.dataobject.DataFieldVersionDO;
import com.hand.hdsp.quality.api.dto.DataFieldVersionDTO;

import io.choerodon.core.convertor.ConvertorI;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


import java.util.Optional;

/**
 * <p>字段标准版本表转换器</p>
 *
 * @author wsl 2020-11-25 15:38:21
 */
@Component
public class DataFieldVersionConverter implements ConvertorI<DataFieldVersion, DataFieldVersionDO, DataFieldVersionDTO> {

    @Override
    public DataFieldVersion dtoToEntity( DataFieldVersionDTO dto) {
        return Optional.ofNullable(dto)
                .map(o -> {
                    DataFieldVersion entity = DataFieldVersion.builder().build();
                    BeanUtils.copyProperties(dto, entity);
                    return entity;
                }).orElseThrow(IllegalArgumentException::new);
    }

    @Override
    public DataFieldVersionDTO entityToDto(DataFieldVersion entity) {

        return Optional.ofNullable(entity)
                .map(o -> {
                    DataFieldVersionDTO dto= DataFieldVersionDTO.builder().build();
                    BeanUtils.copyProperties(entity,dto);
                    return dto;
                }).orElseThrow(IllegalArgumentException::new);
    }
}