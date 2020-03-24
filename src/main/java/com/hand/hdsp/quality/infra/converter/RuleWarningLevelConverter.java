package com.hand.hdsp.quality.infra.converter;

import com.hand.hdsp.quality.api.dto.RuleWarningLevelDTO;
import com.hand.hdsp.quality.domain.entity.RuleWarningLevel;
import com.hand.hdsp.quality.infra.dataobject.RuleWarningLevelDO;
import io.choerodon.core.convertor.ConvertorI;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * <p>规则告警等级表转换器</p>
 *
 * @author feng.liu01@hand-china.com 2020-03-24 09:31:46
 */
@Component
public class RuleWarningLevelConverter implements ConvertorI<RuleWarningLevel, RuleWarningLevelDO, RuleWarningLevelDTO> {

    @Override
    public RuleWarningLevel dtoToEntity(RuleWarningLevelDTO dto) {
        return Optional.ofNullable(dto)
                .map(o -> {
                    RuleWarningLevel entity = RuleWarningLevel.builder().build();
                    BeanUtils.copyProperties(dto, entity);
                    return entity;
                }).orElseThrow(IllegalArgumentException::new);
    }

    @Override
    public RuleWarningLevelDTO entityToDto(RuleWarningLevel entity) {

        return Optional.ofNullable(entity)
                .map(o -> {
                    RuleWarningLevelDTO dto = RuleWarningLevelDTO.builder().build();
                    BeanUtils.copyProperties(entity, dto);
                    return dto;
                }).orElseThrow(IllegalArgumentException::new);
    }
}
