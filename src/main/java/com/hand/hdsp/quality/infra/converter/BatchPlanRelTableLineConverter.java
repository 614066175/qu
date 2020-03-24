package com.hand.hdsp.quality.infra.converter;

import com.hand.hdsp.quality.api.dto.BatchPlanRelTableLineDTO;
import com.hand.hdsp.quality.domain.entity.BatchPlanRelTableLine;
import com.hand.hdsp.quality.infra.dataobject.BatchPlanRelTableLineDO;
import io.choerodon.core.convertor.ConvertorI;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * <p>批数据方案-表间规则关联关系表转换器</p>
 *
 * @author feng.liu01@hand-china.com 2020-03-24 16:19:53
 */
@Component
public class BatchPlanRelTableLineConverter implements ConvertorI<BatchPlanRelTableLine, BatchPlanRelTableLineDO, BatchPlanRelTableLineDTO> {

    @Override
    public BatchPlanRelTableLine dtoToEntity(BatchPlanRelTableLineDTO dto) {
        return Optional.ofNullable(dto)
                .map(o -> {
                    BatchPlanRelTableLine entity = BatchPlanRelTableLine.builder().build();
                    BeanUtils.copyProperties(dto, entity);
                    return entity;
                }).orElseThrow(IllegalArgumentException::new);
    }

    @Override
    public BatchPlanRelTableLineDTO entityToDto(BatchPlanRelTableLine entity) {

        return Optional.ofNullable(entity)
                .map(o -> {
                    BatchPlanRelTableLineDTO dto = BatchPlanRelTableLineDTO.builder().build();
                    BeanUtils.copyProperties(entity, dto);
                    return dto;
                }).orElseThrow(IllegalArgumentException::new);
    }
}
