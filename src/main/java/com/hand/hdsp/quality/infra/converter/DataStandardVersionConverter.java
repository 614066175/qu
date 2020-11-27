package com.hand.hdsp.quality.infra.converter;

import java.util.Optional;

import com.hand.hdsp.quality.api.dto.DataStandardVersionDTO;
import com.hand.hdsp.quality.domain.entity.DataStandardVersion;
import com.hand.hdsp.quality.infra.dataobject.DataStandardVersionDO;
import io.choerodon.core.convertor.ConvertorI;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

/**
 * <p>数据标准版本表转换器</p>
 *
 * @author guoliangli01@hand-china.com 2020-11-27 14:36:44
 */
@Component
public class DataStandardVersionConverter implements ConvertorI<DataStandardVersion, DataStandardVersionDO, DataStandardVersionDTO> {

    @Override
    public DataStandardVersion dtoToEntity(DataStandardVersionDTO dto) {
        return Optional.ofNullable(dto)
                .map(o -> {
                    DataStandardVersion entity = DataStandardVersion.builder().build();
                    BeanUtils.copyProperties(dto, entity);
                    return entity;
                }).orElseThrow(IllegalArgumentException::new);
    }

    @Override
    public DataStandardVersionDTO entityToDto(DataStandardVersion entity) {

        return Optional.ofNullable(entity)
                .map(o -> {
                    DataStandardVersionDTO dto= DataStandardVersionDTO.builder().build();
                    BeanUtils.copyProperties(entity,dto);
                    return dto;
                }).orElseThrow(IllegalArgumentException::new);
    }
}