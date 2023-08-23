package org.xdsp.quality.infra.converter;

import io.choerodon.core.convertor.ConvertorI;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;
import org.xdsp.quality.api.dto.StandardDocDTO;
import org.xdsp.quality.domain.entity.StandardDoc;
import org.xdsp.quality.infra.dataobject.StandardDocDO;

import java.util.Optional;

/**
 * <p>标准文档管理表转换器</p>
 *
 * @author hua.shi@hand-china.com 2020-11-24 17:50:14
 */
@Component
public class StandardDocConverter implements ConvertorI<StandardDoc, StandardDocDO, StandardDocDTO> {

    @Override
    public StandardDoc dtoToEntity(StandardDocDTO dto) {
        return Optional.ofNullable(dto)
                .map(o -> {
                    StandardDoc entity = StandardDoc.builder().build();
                    BeanUtils.copyProperties(dto, entity);
                    return entity;
                }).orElseThrow(IllegalArgumentException::new);
    }

    @Override
    public StandardDocDTO entityToDto(StandardDoc entity) {

        return Optional.ofNullable(entity)
                .map(o -> {
                    StandardDocDTO dto = StandardDocDTO.builder().build();
                    BeanUtils.copyProperties(entity, dto);
                    return dto;
                }).orElseThrow(IllegalArgumentException::new);
    }
}