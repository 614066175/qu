package com.hand.hdsp.quality.infra.converter;

import com.hand.hdsp.quality.api.dto.BatchPlanRelTableDTO;
import com.hand.hdsp.quality.domain.entity.BatchPlanRelTable;
import com.hand.hdsp.quality.infra.dataobject.BatchPlanRelTableDO;
import io.choerodon.core.convertor.ConvertorI;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

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
