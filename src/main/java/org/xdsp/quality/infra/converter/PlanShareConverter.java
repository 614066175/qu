package org.xdsp.quality.infra.converter;

import io.choerodon.core.convertor.ConvertorI;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;
import org.xdsp.quality.api.dto.PlanShareDTO;
import org.xdsp.quality.domain.entity.PlanShare;
import org.xdsp.quality.infra.dataobject.PlanShareDO;

import java.util.Optional;

/**
 * <p>转换器</p>
 *
 * @author guoliang.li01@hand-china.com 2022-09-26 14:04:11
 */
@Component
public class PlanShareConverter implements ConvertorI<PlanShare, PlanShareDO, PlanShareDTO> {

    @Override
    public PlanShare dtoToEntity( PlanShareDTO dto) {
        return Optional.ofNullable(dto)
                .map(o -> {
                    PlanShare entity = PlanShare.builder().build();
                    BeanUtils.copyProperties(dto, entity);
                    return entity;
                }).orElseThrow(IllegalArgumentException::new);
    }

    @Override
    public PlanShareDTO entityToDto(PlanShare entity) {

        return Optional.ofNullable(entity)
                .map(o -> {
                    PlanShareDTO dto= PlanShareDTO.builder().build();
                    BeanUtils.copyProperties(entity,dto);
                    return dto;
                }).orElseThrow(IllegalArgumentException::new);
    }
}