package org.xdsp.quality.infra.converter;

import io.choerodon.core.convertor.ConvertorI;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;
import org.xdsp.quality.api.dto.BatchResultRuleDTO;
import org.xdsp.quality.domain.entity.BatchResultRule;
import org.xdsp.quality.infra.dataobject.BatchResultRuleDO;

import java.util.Optional;

/**
 * <p>批数据方案结果表-规则信息转换器</p>
 *
 * @author feng.liu01@hand-china.com 2020-03-24 16:19:52
 */
@Component
public class BatchResultRuleConverter implements ConvertorI<BatchResultRule, BatchResultRuleDO, BatchResultRuleDTO> {

    @Override
    public BatchResultRule dtoToEntity(BatchResultRuleDTO dto) {
        return Optional.ofNullable(dto)
                .map(o -> {
                    BatchResultRule entity = BatchResultRule.builder().build();
                    BeanUtils.copyProperties(dto, entity);
                    return entity;
                }).orElseThrow(IllegalArgumentException::new);
    }

    @Override
    public BatchResultRuleDTO entityToDto(BatchResultRule entity) {

        return Optional.ofNullable(entity)
                .map(o -> {
                    BatchResultRuleDTO dto = BatchResultRuleDTO.builder().build();
                    BeanUtils.copyProperties(entity, dto);
                    return dto;
                }).orElseThrow(IllegalArgumentException::new);
    }
}
