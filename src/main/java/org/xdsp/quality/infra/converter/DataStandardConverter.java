package org.xdsp.quality.infra.converter;

import io.choerodon.core.convertor.ConvertorI;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;
import org.xdsp.quality.api.dto.DataStandardDTO;
import org.xdsp.quality.domain.entity.DataStandard;
import org.xdsp.quality.infra.dataobject.DataStandardDO;

import java.util.Optional;

/**
 * <p>数据标准表转换器</p>
 *
 * @author guoliangli01@hand-china.com 2020-11-27 14:36:44
 */
@Component
public class DataStandardConverter implements ConvertorI<DataStandard, DataStandardDO, DataStandardDTO> {

    @Override
    public DataStandard dtoToEntity(DataStandardDTO dto) {
        return Optional.ofNullable(dto)
                .map(o -> {
                    DataStandard entity = DataStandard.builder().build();
                    BeanUtils.copyProperties(dto, entity);
                    return entity;
                }).orElseThrow(IllegalArgumentException::new);
    }

    @Override
    public DataStandardDTO entityToDto(DataStandard entity) {

        return Optional.ofNullable(entity)
                .map(o -> {
                    DataStandardDTO dto= DataStandardDTO.builder().build();
                    BeanUtils.copyProperties(entity,dto);
                    return dto;
                }).orElseThrow(IllegalArgumentException::new);
    }
}