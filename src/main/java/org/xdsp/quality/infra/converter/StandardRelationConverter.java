package org.xdsp.quality.infra.converter;

import io.choerodon.core.convertor.ConvertorI;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;
import org.xdsp.quality.api.dto.StandardRelationDTO;
import org.xdsp.quality.domain.entity.StandardRelation;
import org.xdsp.quality.infra.dataobject.StandardRelationDO;

import java.util.Optional;

/**
 * <p>标准-标准组关系表转换器</p>
 *
 * @author guoliang.li01@hand-china.com 2022-05-09 15:50:16
 */
@Component
public class StandardRelationConverter implements ConvertorI<StandardRelation, StandardRelationDO, StandardRelationDTO> {

    @Override
    public StandardRelation dtoToEntity( StandardRelationDTO dto) {
        return Optional.ofNullable(dto)
                .map(o -> {
                    StandardRelation entity = StandardRelation.builder().build();
                    BeanUtils.copyProperties(dto, entity);
                    return entity;
                }).orElseThrow(IllegalArgumentException::new);
    }

    @Override
    public StandardRelationDTO entityToDto(StandardRelation entity) {

        return Optional.ofNullable(entity)
                .map(o -> {
                    StandardRelationDTO dto= StandardRelationDTO.builder().build();
                    BeanUtils.copyProperties(entity,dto);
                    return dto;
                }).orElseThrow(IllegalArgumentException::new);
    }
}