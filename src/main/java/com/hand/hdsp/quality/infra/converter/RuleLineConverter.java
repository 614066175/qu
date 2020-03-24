package com.hand.hdsp.quality.infra.converter;

import com.hand.hdsp.quality.api.dto.RuleLineDTO;
import com.hand.hdsp.quality.domain.entity.RuleLine;
import com.hand.hdsp.quality.infra.dataobject.RuleLineDO;
import io.choerodon.core.convertor.ConvertorI;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * <p>规则校验项表转换器</p>
 *
 * @author feng.liu01@hand-china.com 2020-03-24 09:31:46
 */
@Component
public class RuleLineConverter implements ConvertorI<RuleLine, RuleLineDO, RuleLineDTO> {

    @Override
    public RuleLine dtoToEntity(RuleLineDTO dto) {
        return Optional.ofNullable(dto)
                .map(o -> {
                    RuleLine entity = RuleLine.builder().build();
                    BeanUtils.copyProperties(dto, entity);
                    return entity;
                }).orElseThrow(IllegalArgumentException::new);
    }

    @Override
    public RuleLineDTO entityToDto(RuleLine entity) {

        return Optional.ofNullable(entity)
                .map(o -> {
                    RuleLineDTO dto = RuleLineDTO.builder().build();
                    BeanUtils.copyProperties(entity, dto);
                    return dto;
                }).orElseThrow(IllegalArgumentException::new);
    }
}
