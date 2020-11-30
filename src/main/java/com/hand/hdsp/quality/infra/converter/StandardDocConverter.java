package com.hand.hdsp.quality.infra.converter;

import java.util.Optional;

import com.hand.hdsp.quality.api.dto.StandardDocDTO;
import com.hand.hdsp.quality.domain.entity.StandardDoc;
import com.hand.hdsp.quality.infra.dataobject.StandardDocDO;
import io.choerodon.core.convertor.ConvertorI;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

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