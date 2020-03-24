package com.hand.hdsp.quality.infra.converter;

import com.hand.hdsp.quality.api.dto.StreamingResultRuleDTO;
import com.hand.hdsp.quality.domain.entity.StreamingResultRule;
import com.hand.hdsp.quality.infra.dataobject.StreamingResultRuleDO;
import io.choerodon.core.convertor.ConvertorI;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * <p>实时数据方案结果表-规则信息转换器</p>
 *
 * @author feng.liu01@hand-china.com 2020-03-24 16:19:50
 */
@Component
public class StreamingResultRuleConverter implements ConvertorI<StreamingResultRule, StreamingResultRuleDO, StreamingResultRuleDTO> {

    @Override
    public StreamingResultRule dtoToEntity(StreamingResultRuleDTO dto) {
        return Optional.ofNullable(dto)
                .map(o -> {
                    StreamingResultRule entity = StreamingResultRule.builder().build();
                    BeanUtils.copyProperties(dto, entity);
                    return entity;
                }).orElseThrow(IllegalArgumentException::new);
    }

    @Override
    public StreamingResultRuleDTO entityToDto(StreamingResultRule entity) {

        return Optional.ofNullable(entity)
                .map(o -> {
                    StreamingResultRuleDTO dto = StreamingResultRuleDTO.builder().build();
                    BeanUtils.copyProperties(entity, dto);
                    return dto;
                }).orElseThrow(IllegalArgumentException::new);
    }
}
