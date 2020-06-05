package com.hand.hdsp.quality.infra.converter;

import com.hand.hdsp.quality.api.dto.BatchPlanFieldConDTO;
import com.hand.hdsp.quality.domain.entity.BatchPlanFieldCon;
import com.hand.hdsp.quality.infra.dataobject.BatchPlanFieldConDO;
import io.choerodon.core.convertor.ConvertorI;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

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
