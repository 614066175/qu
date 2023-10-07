package org.xdsp.quality.infra.converter;

import io.choerodon.core.convertor.ConvertorI;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;
import org.xdsp.quality.api.dto.BatchPlanTableDTO;
import org.xdsp.quality.domain.entity.BatchPlanTable;
import org.xdsp.quality.infra.dataobject.BatchPlanTableDO;

import java.util.Optional;

/**
 * <p>批数据方案-表级规则表转换器</p>
 *
 * @author feng.liu01@hand-china.com 2020-03-24 16:19:52
 */
@Component
public class BatchPlanTableConverter implements ConvertorI<BatchPlanTable, BatchPlanTableDO, BatchPlanTableDTO> {

    @Override
    public BatchPlanTable dtoToEntity(BatchPlanTableDTO dto) {
        return Optional.ofNullable(dto)
                .map(o -> {
                    BatchPlanTable entity = BatchPlanTable.builder().build();
                    BeanUtils.copyProperties(dto, entity);
                    return entity;
                }).orElseThrow(IllegalArgumentException::new);
    }

    @Override
    public BatchPlanTableDTO entityToDto(BatchPlanTable entity) {

        return Optional.ofNullable(entity)
                .map(o -> {
                    BatchPlanTableDTO dto = BatchPlanTableDTO.builder().build();
                    BeanUtils.copyProperties(entity, dto);
                    return dto;
                }).orElseThrow(IllegalArgumentException::new);
    }
}
