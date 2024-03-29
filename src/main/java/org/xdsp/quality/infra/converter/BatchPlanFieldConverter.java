package org.xdsp.quality.infra.converter;

import io.choerodon.core.convertor.ConvertorI;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;
import org.xdsp.quality.api.dto.BatchPlanFieldDTO;
import org.xdsp.quality.domain.entity.BatchPlanField;
import org.xdsp.quality.infra.dataobject.BatchPlanFieldDO;

import java.util.Optional;

/**
 * <p>批数据方案-字段规则表转换器</p>
 *
 * @author feng.liu01@hand-china.com 2020-03-24 16:19:53
 */
@Component
public class BatchPlanFieldConverter implements ConvertorI<BatchPlanField, BatchPlanFieldDO, BatchPlanFieldDTO> {

    @Override
    public BatchPlanField dtoToEntity(BatchPlanFieldDTO dto) {
        return Optional.ofNullable(dto)
                .map(o -> {
                    BatchPlanField entity = BatchPlanField.builder().build();
                    BeanUtils.copyProperties(dto, entity);
                    return entity;
                }).orElseThrow(IllegalArgumentException::new);
    }

    @Override
    public BatchPlanFieldDTO entityToDto(BatchPlanField entity) {

        return Optional.ofNullable(entity)
                .map(o -> {
                    BatchPlanFieldDTO dto = BatchPlanFieldDTO.builder().build();
                    BeanUtils.copyProperties(entity, dto);
                    return dto;
                }).orElseThrow(IllegalArgumentException::new);
    }
}
