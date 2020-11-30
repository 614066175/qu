package com.hand.hdsp.quality.infra.converter;

import java.util.Optional;

import com.hand.hdsp.quality.api.dto.StanardAimRelationDTO;
import com.hand.hdsp.quality.domain.entity.StanardAimRelation;
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
public class StanardAimRelationConverter implements ConvertorI<StanardAimRelation, StanardAimRelationDO, StanardAimRelationDTO> {

    @Override
    public StanardAimRelation dtoToEntity(StanardAimRelationDTO dto) {
        return Optional.ofNullable(dto)
                .map(o -> {
                    StanardAimRelation entity = StanardAimRelation.builder().build();
                    BeanUtils.copyProperties(dto, entity);
                    return entity;
                }).orElseThrow(IllegalArgumentException::new);
    }

    @Override
    public StanardAimRelationDTO entityToDto(StanardAimRelation entity) {

        return Optional.ofNullable(entity)
                .map(o -> {
                    StanardAimRelationDTO dto= StanardAimRelationDTO.builder().build();
                    BeanUtils.copyProperties(entity,dto);
                    return dto;
                }).orElseThrow(IllegalArgumentException::new);
    }
}