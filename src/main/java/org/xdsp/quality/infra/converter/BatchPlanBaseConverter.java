package org.xdsp.quality.infra.converter;

import io.choerodon.core.convertor.ConvertorI;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;
import org.xdsp.quality.api.dto.BatchPlanBaseDTO;
import org.xdsp.quality.domain.entity.BatchPlanBase;
import org.xdsp.quality.infra.dataobject.BatchPlanBaseDO;

import java.util.Optional;

/**
 * <p>批数据方案-基础配置表转换器</p>
 *
 * @author feng.liu01@hand-china.com 2020-03-24 16:19:53
 */
@Component
public class BatchPlanBaseConverter implements ConvertorI<BatchPlanBase, BatchPlanBaseDO, BatchPlanBaseDTO> {

    @Override
    public BatchPlanBase dtoToEntity(BatchPlanBaseDTO dto) {
        return Optional.ofNullable(dto)
                .map(o -> {
                    BatchPlanBase entity = BatchPlanBase.builder().build();
                    BeanUtils.copyProperties(dto, entity);
                    return entity;
                }).orElseThrow(IllegalArgumentException::new);
    }

    @Override
    public BatchPlanBaseDTO entityToDto(BatchPlanBase entity) {

        return Optional.ofNullable(entity)
                .map(o -> {
                    BatchPlanBaseDTO dto = BatchPlanBaseDTO.builder().build();
                    BeanUtils.copyProperties(entity, dto);
                    return dto;
                }).orElseThrow(IllegalArgumentException::new);
    }
}
