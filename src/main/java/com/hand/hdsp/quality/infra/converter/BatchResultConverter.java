package com.hand.hdsp.quality.infra.converter;

import com.hand.hdsp.quality.api.dto.BatchResultDTO;
import com.hand.hdsp.quality.domain.entity.BatchResult;
import com.hand.hdsp.quality.infra.dataobject.BatchResultDO;
import io.choerodon.core.convertor.ConvertorI;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * <p>批数据方案结果表转换器</p>
 *
 * @author feng.liu01@hand-china.com 2020-03-24 16:19:51
 */
@Component
public class BatchResultConverter implements ConvertorI<BatchResult, BatchResultDO, BatchResultDTO> {

    @Override
    public BatchResult dtoToEntity(BatchResultDTO dto) {
        return Optional.ofNullable(dto)
                .map(o -> {
                    BatchResult entity = BatchResult.builder().build();
                    BeanUtils.copyProperties(dto, entity);
                    return entity;
                }).orElseThrow(IllegalArgumentException::new);
    }

    @Override
    public BatchResultDTO entityToDto(BatchResult entity) {

        return Optional.ofNullable(entity)
                .map(o -> {
                    BatchResultDTO dto = BatchResultDTO.builder().build();
                    BeanUtils.copyProperties(entity, dto);
                    return dto;
                }).orElseThrow(IllegalArgumentException::new);
    }
}
