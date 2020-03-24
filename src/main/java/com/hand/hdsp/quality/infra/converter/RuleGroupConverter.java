package com.hand.hdsp.quality.infra.converter;

import com.hand.hdsp.quality.api.dto.RuleGroupDTO;
import com.hand.hdsp.quality.domain.entity.RuleGroup;
import com.hand.hdsp.quality.infra.dataobject.RuleGroupDO;
import io.choerodon.core.convertor.ConvertorI;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * <p>规则分组表转换器</p>
 *
 * @author feng.liu01@hand-china.com 2020-03-24 09:31:47
 */
@Component
public class RuleGroupConverter implements ConvertorI<RuleGroup, RuleGroupDO, RuleGroupDTO> {

    @Override
    public RuleGroup dtoToEntity(RuleGroupDTO dto) {
        return Optional.ofNullable(dto)
                .map(o -> {
                    RuleGroup entity = RuleGroup.builder().build();
                    BeanUtils.copyProperties(dto, entity);
                    return entity;
                }).orElseThrow(IllegalArgumentException::new);
    }

    @Override
    public RuleGroupDTO entityToDto(RuleGroup entity) {

        return Optional.ofNullable(entity)
                .map(o -> {
                    RuleGroupDTO dto = RuleGroupDTO.builder().build();
                    BeanUtils.copyProperties(entity, dto);
                    return dto;
                }).orElseThrow(IllegalArgumentException::new);
    }
}
