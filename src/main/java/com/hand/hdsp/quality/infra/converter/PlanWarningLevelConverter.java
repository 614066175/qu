package com.hand.hdsp.quality.infra.converter;

import com.hand.hdsp.quality.api.dto.PlanWarningLevelDTO;
import com.hand.hdsp.quality.domain.entity.PlanWarningLevel;
import com.hand.hdsp.quality.infra.dataobject.PlanWarningLevelDO;
import io.choerodon.core.convertor.ConvertorI;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * <p>方案告警等级表转换器</p>
 *
 * @author feng.liu01@hand-china.com 2020-03-24 16:19:51
 */
@Component
public class PlanWarningLevelConverter implements ConvertorI<PlanWarningLevel, PlanWarningLevelDO, PlanWarningLevelDTO> {

    @Override
    public PlanWarningLevel dtoToEntity(PlanWarningLevelDTO dto) {
        return Optional.ofNullable(dto)
                .map(o -> {
                    PlanWarningLevel entity = PlanWarningLevel.builder().build();
                    BeanUtils.copyProperties(dto, entity);
                    return entity;
                }).orElseThrow(IllegalArgumentException::new);
    }

    @Override
    public PlanWarningLevelDTO entityToDto(PlanWarningLevel entity) {

        return Optional.ofNullable(entity)
                .map(o -> {
                    PlanWarningLevelDTO dto = PlanWarningLevelDTO.builder().build();
                    BeanUtils.copyProperties(entity, dto);
                    return dto;
                }).orElseThrow(IllegalArgumentException::new);
    }
}
