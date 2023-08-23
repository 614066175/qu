package org.xdsp.quality.infra.converter;

import io.choerodon.core.convertor.ConvertorI;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;
import org.xdsp.quality.api.dto.ReferenceDataDTO;
import org.xdsp.quality.domain.entity.ReferenceData;
import org.xdsp.quality.infra.dataobject.ReferenceDataDO;

import java.util.Optional;

/**
 * <p>参考数据头表转换器</p>
 *
 * @author fuqiang.luo@hand-china.com 2023-04-14 16:23:35
 */
@Component
public class ReferenceDataConverter implements ConvertorI<ReferenceData, ReferenceDataDO, ReferenceDataDTO> {

    @Override
    public ReferenceData dtoToEntity( ReferenceDataDTO dto) {
        return Optional.ofNullable(dto)
                .map(o -> {
                    ReferenceData entity = ReferenceData.builder().build();
                    BeanUtils.copyProperties(dto, entity);
                    return entity;
                }).orElseThrow(IllegalArgumentException::new);
    }

    @Override
    public ReferenceDataDTO entityToDto(ReferenceData entity) {

        return Optional.ofNullable(entity)
                .map(o -> {
                    ReferenceDataDTO dto= ReferenceDataDTO.builder().build();
                    BeanUtils.copyProperties(entity,dto);
                    return dto;
                }).orElseThrow(IllegalArgumentException::new);
    }
}