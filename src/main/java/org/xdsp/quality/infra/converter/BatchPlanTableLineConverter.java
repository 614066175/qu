package org.xdsp.quality.infra.converter;

import io.choerodon.core.convertor.ConvertorI;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;
import org.xdsp.quality.api.dto.BatchPlanTableLineDTO;
import org.xdsp.quality.domain.entity.BatchPlanTableLine;
import org.xdsp.quality.infra.dataobject.BatchPlanTableLineDO;

import java.util.Optional;

/**
 * <p>批数据方案-表级规则校验项表转换器</p>
 *
 * @author feng.liu01@hand-china.com 2020-03-24 16:19:52
 */
@Component
public class BatchPlanTableLineConverter implements ConvertorI<BatchPlanTableLine, BatchPlanTableLineDO, BatchPlanTableLineDTO> {

    @Override
    public BatchPlanTableLine dtoToEntity(BatchPlanTableLineDTO dto) {
        return Optional.ofNullable(dto)
                .map(o -> {
                    BatchPlanTableLine entity = BatchPlanTableLine.builder().build();
                    BeanUtils.copyProperties(dto, entity);
                    return entity;
                }).orElseThrow(IllegalArgumentException::new);
    }

    @Override
    public BatchPlanTableLineDTO entityToDto(BatchPlanTableLine entity) {

        return Optional.ofNullable(entity)
                .map(o -> {
                    BatchPlanTableLineDTO dto = BatchPlanTableLineDTO.builder().build();
                    BeanUtils.copyProperties(entity, dto);
                    return dto;
                }).orElseThrow(IllegalArgumentException::new);
    }
}
