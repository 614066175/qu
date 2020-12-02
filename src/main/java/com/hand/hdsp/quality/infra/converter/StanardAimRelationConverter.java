package com.hand.hdsp.quality.infra.converter;

import java.util.Optional;

import com.hand.hdsp.quality.api.dto.StandardAimRelationDTO;
import com.hand.hdsp.quality.domain.entity.StandardAimRelation;
import com.hand.hdsp.quality.infra.dataobject.StanardAimRelationDO;
import io.choerodon.core.convertor.ConvertorI;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

/**
 * <p>标准落标关系表转换器</p>
 *
 * @author guoliangli01@hand-china.com 2020-11-30 10:23:51
 */
@Component
public class StanardAimRelationConverter implements ConvertorI<StandardAimRelation, StanardAimRelationDO, StandardAimRelationDTO> {

    @Override
    public StandardAimRelation dtoToEntity(StandardAimRelationDTO dto) {
        return Optional.ofNullable(dto)
                .map(o -> {
                    StandardAimRelation entity = StandardAimRelation.builder().build();
                    BeanUtils.copyProperties(dto, entity);
                    return entity;
                }).orElseThrow(IllegalArgumentException::new);
    }

    @Override
    public StandardAimRelationDTO entityToDto(StandardAimRelation entity) {

        return Optional.ofNullable(entity)
                .map(o -> {
                    StandardAimRelationDTO dto= StandardAimRelationDTO.builder().build();
                    BeanUtils.copyProperties(entity,dto);
                    return dto;
                }).orElseThrow(IllegalArgumentException::new);
    }
}