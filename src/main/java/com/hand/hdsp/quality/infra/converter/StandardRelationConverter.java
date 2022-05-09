package com.hand.hdsp.quality.infra.converter;

import com.hand.hdsp.quality.domain.entity.StandardRelation;
import com.hand.hdsp.quality.infra.dataobject.StandardRelationDO;
import com.hand.hdsp.quality.api.dto.StandardRelationDTO;

import io.choerodon.core.convertor.ConvertorI;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


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