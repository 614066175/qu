package com.hand.hdsp.quality.infra.converter;

import com.hand.hdsp.quality.domain.entity.NameAimRule;
import com.hand.hdsp.quality.infra.dataobject.NameAimRuleDO;
import com.hand.hdsp.quality.api.dto.NameAimRuleDTO;

import io.choerodon.core.convertor.ConvertorI;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


import java.util.Optional;

/**
 * <p>命名标准排除规则表转换器</p>
 *
 * @author 张鹏 2020-11-27 16:35:40
 */
@Component
public class NameAimRuleConverter implements ConvertorI<NameAimRule, NameAimRuleDO, NameAimRuleDTO> {

    @Override
    public NameAimRule dtoToEntity( NameAimRuleDTO dto) {
        return Optional.ofNullable(dto)
                .map(o -> {
                    NameAimRule entity = NameAimRule.builder().build();
                    BeanUtils.copyProperties(dto, entity);
                    return entity;
                }).orElseThrow(IllegalArgumentException::new);
    }

    @Override
    public NameAimRuleDTO entityToDto(NameAimRule entity) {

        return Optional.ofNullable(entity)
                .map(o -> {
                    NameAimRuleDTO dto= NameAimRuleDTO.builder().build();
                    BeanUtils.copyProperties(entity,dto);
                    return dto;
                }).orElseThrow(IllegalArgumentException::new);
    }
}