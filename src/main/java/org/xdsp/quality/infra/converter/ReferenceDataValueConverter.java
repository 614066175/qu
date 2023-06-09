package org.xdsp.quality.infra.converter;

import io.choerodon.core.convertor.ConvertorI;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;
import org.xdsp.quality.api.dto.ReferenceDataValueDTO;
import org.xdsp.quality.domain.entity.ReferenceDataValue;
import org.xdsp.quality.infra.dataobject.ReferenceDataValueDO;

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