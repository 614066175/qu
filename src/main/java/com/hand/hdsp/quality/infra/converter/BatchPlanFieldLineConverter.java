package com.hand.hdsp.quality.infra.converter;

import com.hand.hdsp.quality.api.dto.BatchPlanFieldLineDTO;
import com.hand.hdsp.quality.domain.entity.BatchPlanFieldLine;
import com.hand.hdsp.quality.infra.dataobject.BatchPlanFieldLineDO;
import io.choerodon.core.convertor.ConvertorI;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * <p>批数据方案-字段规则校验项表转换器</p>
 *
 * @author feng.liu01@hand-china.com 2020-03-24 16:19:53
 */
@Component
public class BatchPlanFieldLineConverter implements ConvertorI<BatchPlanFieldLine, BatchPlanFieldLineDO, BatchPlanFieldLineDTO> {

    @Override
    public BatchPlanFieldLine dtoToEntity(BatchPlanFieldLineDTO dto) {
        return Optional.ofNullable(dto)
                .map(o -> {
                    BatchPlanFieldLine entity = BatchPlanFieldLine.builder().build();
                    BeanUtils.copyProperties(dto, entity);
                    return entity;
                }).orElseThrow(IllegalArgumentException::new);
    }

    @Override
    public BatchPlanFieldLineDTO entityToDto(BatchPlanFieldLine entity) {

        return Optional.ofNullable(entity)
                .map(o -> {
                    BatchPlanFieldLineDTO dto = BatchPlanFieldLineDTO.builder().build();
                    BeanUtils.copyProperties(entity, dto);
                    return dto;
                }).orElseThrow(IllegalArgumentException::new);
    }
}
