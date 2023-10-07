package org.xdsp.quality.infra.converter;

import io.choerodon.core.convertor.ConvertorI;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;
import org.xdsp.quality.api.dto.PlanGroupDTO;
import org.xdsp.quality.domain.entity.PlanGroup;
import org.xdsp.quality.infra.dataobject.PlanGroupDO;

import java.util.Optional;

/**
 * <p>评估方案分组表转换器</p>
 *
 * @author feng.liu01@hand-china.com 2020-03-24 16:19:51
 */
@Component
public class PlanGroupConverter implements ConvertorI<PlanGroup, PlanGroupDO, PlanGroupDTO> {

    @Override
    public PlanGroup dtoToEntity(PlanGroupDTO dto) {
        return Optional.ofNullable(dto)
                .map(o -> {
                    PlanGroup entity = PlanGroup.builder().build();
                    BeanUtils.copyProperties(dto, entity);
                    return entity;
                }).orElseThrow(IllegalArgumentException::new);
    }

    @Override
    public PlanGroupDTO entityToDto(PlanGroup entity) {

        return Optional.ofNullable(entity)
                .map(o -> {
                    PlanGroupDTO dto = PlanGroupDTO.builder().build();
                    BeanUtils.copyProperties(entity, dto);
                    return dto;
                }).orElseThrow(IllegalArgumentException::new);
    }
}
