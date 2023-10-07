package org.xdsp.quality.infra.converter;

import io.choerodon.core.convertor.ConvertorI;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;
import org.xdsp.quality.api.dto.BatchPlanRelTableDTO;
import org.xdsp.quality.domain.entity.BatchPlanRelTable;
import org.xdsp.quality.infra.dataobject.BatchPlanRelTableDO;

import java.util.Optional;

/**
 * <p>批数据方案-表间规则表转换器</p>
 *
 * @author feng.liu01@hand-china.com 2020-03-24 16:19:53
 */
@Component
public class BatchPlanRelTableConverter implements ConvertorI<BatchPlanRelTable, BatchPlanRelTableDO, BatchPlanRelTableDTO> {

    @Override
    public BatchPlanRelTable dtoToEntity(BatchPlanRelTableDTO dto) {
        return Optional.ofNullable(dto)
                .map(o -> {
                    BatchPlanRelTable entity = BatchPlanRelTable.builder().build();
                    BeanUtils.copyProperties(dto, entity);
                    return entity;
                }).orElseThrow(IllegalArgumentException::new);
    }

    @Override
    public BatchPlanRelTableDTO entityToDto(BatchPlanRelTable entity) {

        return Optional.ofNullable(entity)
                .map(o -> {
                    BatchPlanRelTableDTO dto = BatchPlanRelTableDTO.builder().build();
                    BeanUtils.copyProperties(entity, dto);
                    return dto;
                }).orElseThrow(IllegalArgumentException::new);
    }
}
