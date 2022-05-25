package com.hand.hdsp.quality.infra.converter;

import com.hand.hdsp.quality.api.dto.PlanBaseAssignDTO;
import com.hand.hdsp.quality.domain.entity.PlanBaseAssign;
import com.hand.hdsp.quality.infra.dataobject.PlanBaseAssignDO;
import io.choerodon.core.convertor.ConvertorI;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * <p>质检项分配表转换器</p>
 *
 * @author guoliang.li01@hand-china.com 2022-05-20 15:36:10
 */
@Component
public class PlanBaseAssignConverter implements ConvertorI<PlanBaseAssign, PlanBaseAssignDO, PlanBaseAssignDTO> {

    @Override
    public PlanBaseAssign dtoToEntity(PlanBaseAssignDTO dto) {
        return Optional.ofNullable(dto)
                .map(o -> {
                    PlanBaseAssign entity = PlanBaseAssign.builder().build();
                    BeanUtils.copyProperties(dto, entity);
                    return entity;
                }).orElseThrow(IllegalArgumentException::new);
    }

    @Override
    public PlanBaseAssignDTO entityToDto(PlanBaseAssign entity) {

        return Optional.ofNullable(entity)
                .map(o -> {
                    PlanBaseAssignDTO dto = PlanBaseAssignDTO.builder().build();
                    BeanUtils.copyProperties(entity, dto);
                    return dto;
                }).orElseThrow(IllegalArgumentException::new);
    }
}