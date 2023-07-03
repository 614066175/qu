package org.xdsp.quality.infra.converter;

import io.choerodon.core.convertor.ConvertorI;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;
import org.xdsp.quality.api.dto.BatchPlanFieldConDTO;
import org.xdsp.quality.domain.entity.BatchPlanFieldCon;
import org.xdsp.quality.infra.dataobject.BatchPlanFieldConDO;

import java.util.Optional;

/**
 * <p>批数据方案-字段规则条件表转换器</p>
 *
 * @author feng.liu01@hand-china.com 2020-06-03 20:03:41
 */
@Component
public class BatchPlanFieldConConverter implements ConvertorI<BatchPlanFieldCon, BatchPlanFieldConDO, BatchPlanFieldConDTO> {

    @Override
    public BatchPlanFieldCon dtoToEntity(BatchPlanFieldConDTO dto) {
        return Optional.ofNullable(dto)
                .map(o -> {
                    BatchPlanFieldCon entity = BatchPlanFieldCon.builder().build();
                    BeanUtils.copyProperties(dto, entity);
                    return entity;
                }).orElseThrow(IllegalArgumentException::new);
    }

    @Override
    public BatchPlanFieldConDTO entityToDto(BatchPlanFieldCon entity) {

        return Optional.ofNullable(entity)
                .map(o -> {
                    BatchPlanFieldConDTO dto = BatchPlanFieldConDTO.builder().build();
                    BeanUtils.copyProperties(entity, dto);
                    return dto;
                }).orElseThrow(IllegalArgumentException::new);
    }
}
