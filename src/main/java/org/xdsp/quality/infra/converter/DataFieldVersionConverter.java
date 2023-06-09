package org.xdsp.quality.infra.converter;

import io.choerodon.core.convertor.ConvertorI;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;
import org.xdsp.quality.api.dto.DataFieldVersionDTO;
import org.xdsp.quality.domain.entity.DataFieldVersion;
import org.xdsp.quality.infra.dataobject.DataFieldVersionDO;

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