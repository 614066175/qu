package com.hand.hdsp.quality.infra.converter;

import com.hand.hdsp.quality.api.dto.BatchPlanTableConDTO;
import com.hand.hdsp.quality.domain.entity.BatchPlanTableCon;
import com.hand.hdsp.quality.infra.dataobject.BatchPlanTableConDO;
import io.choerodon.core.convertor.ConvertorI;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * <p>批数据方案-表级规则条件表转换器</p>
 *
 * @author feng.liu01@hand-china.com 2020-06-03 20:03:41
 */
@Component
public class BatchPlanTableConConverter implements ConvertorI<BatchPlanTableCon, BatchPlanTableConDO, BatchPlanTableConDTO> {

    @Override
    public BatchPlanTableCon dtoToEntity(BatchPlanTableConDTO dto) {
        return Optional.ofNullable(dto)
                .map(o -> {
                    BatchPlanTableCon entity = BatchPlanTableCon.builder().build();
                    BeanUtils.copyProperties(dto, entity);
                    return entity;
                }).orElseThrow(IllegalArgumentException::new);
    }

    @Override
    public BatchPlanTableConDTO entityToDto(BatchPlanTableCon entity) {

        return Optional.ofNullable(entity)
                .map(o -> {
                    BatchPlanTableConDTO dto = BatchPlanTableConDTO.builder().build();
                    BeanUtils.copyProperties(entity, dto);
                    return dto;
                }).orElseThrow(IllegalArgumentException::new);
    }
}
