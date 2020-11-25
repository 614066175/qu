package com.hand.hdsp.quality.infra.converter;

import java.util.Optional;

import com.hand.hdsp.quality.api.dto.DataStandardDTO;
import com.hand.hdsp.quality.domain.entity.DataStandard;
import com.hand.hdsp.quality.infra.dataobject.DataStandardDO;
import io.choerodon.core.convertor.ConvertorI;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

/**
 * <p>数据标准表转换器</p>
 *
 * @author guoliangli01.@hand-china.com 2020-11-25 17:03:11
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