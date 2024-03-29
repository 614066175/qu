package org.xdsp.quality.infra.converter;

import io.choerodon.core.convertor.ConvertorI;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;
import org.xdsp.quality.api.dto.StreamingPlanRuleDTO;
import org.xdsp.quality.domain.entity.StreamingPlanRule;
import org.xdsp.quality.infra.dataobject.StreamingPlanRuleDO;

import java.util.Optional;

/**
 * <p>实时数据方案-规则表转换器</p>
 *
 * @author feng.liu01@hand-china.com 2020-03-24 16:19:51
 */
@Component
public class StreamingPlanRuleConverter implements ConvertorI<StreamingPlanRule, StreamingPlanRuleDO, StreamingPlanRuleDTO> {

    @Override
    public StreamingPlanRule dtoToEntity(StreamingPlanRuleDTO dto) {
        return Optional.ofNullable(dto)
                .map(o -> {
                    StreamingPlanRule entity = StreamingPlanRule.builder().build();
                    BeanUtils.copyProperties(dto, entity);
                    return entity;
                }).orElseThrow(IllegalArgumentException::new);
    }

    @Override
    public StreamingPlanRuleDTO entityToDto(StreamingPlanRule entity) {

        return Optional.ofNullable(entity)
                .map(o -> {
                    StreamingPlanRuleDTO dto = StreamingPlanRuleDTO.builder().build();
                    BeanUtils.copyProperties(entity, dto);
                    return dto;
                }).orElseThrow(IllegalArgumentException::new);
    }
}
