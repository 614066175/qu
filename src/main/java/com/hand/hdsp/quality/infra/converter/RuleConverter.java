package com.hand.hdsp.quality.infra.converter;

import com.hand.hdsp.quality.api.dto.RuleDTO;
import com.hand.hdsp.quality.domain.entity.Rule;
import com.hand.hdsp.quality.infra.dataobject.RuleDO;
import io.choerodon.core.convertor.ConvertorI;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * <p>规则表转换器</p>
 *
 * @author feng.liu01@hand-china.com 2020-03-24 09:31:46
 */
@Component
public class RuleConverter implements ConvertorI<Rule, RuleDO, RuleDTO> {

    @Override
    public Rule dtoToEntity(RuleDTO dto) {
        return Optional.ofNullable(dto)
                .map(o -> {
                    Rule entity = Rule.builder().build();
                    BeanUtils.copyProperties(dto, entity);
                    return entity;
                }).orElseThrow(IllegalArgumentException::new);
    }

    @Override
    public RuleDTO entityToDto(Rule entity) {

        return Optional.ofNullable(entity)
                .map(o -> {
                    RuleDTO dto = RuleDTO.builder().build();
                    BeanUtils.copyProperties(entity, dto);
                    return dto;
                }).orElseThrow(IllegalArgumentException::new);
    }
}
