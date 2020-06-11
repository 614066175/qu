package com.hand.hdsp.quality.infra.converter;

import com.hand.hdsp.quality.api.dto.BatchResultItemDTO;
import com.hand.hdsp.quality.domain.entity.BatchResultItem;
import com.hand.hdsp.quality.infra.dataobject.BatchResultItemDO;
import io.choerodon.core.convertor.ConvertorI;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * <p>批数据方案结果表-校验项信息转换器</p>
 *
 * @author feng.liu01@hand-china.com 2020-06-03 20:28:29
 */
@Component
public class BatchResultItemConverter implements ConvertorI<BatchResultItem, BatchResultItemDO, BatchResultItemDTO> {

    @Override
    public BatchResultItem dtoToEntity(BatchResultItemDTO dto) {
        return Optional.ofNullable(dto)
                .map(o -> {
                    BatchResultItem entity = BatchResultItem.builder().build();
                    BeanUtils.copyProperties(dto, entity);
                    return entity;
                }).orElseThrow(IllegalArgumentException::new);
    }

    @Override
    public BatchResultItemDTO entityToDto(BatchResultItem entity) {

        return Optional.ofNullable(entity)
                .map(o -> {
                    BatchResultItemDTO dto = BatchResultItemDTO.builder().build();
                    BeanUtils.copyProperties(entity, dto);
                    return dto;
                }).orElseThrow(IllegalArgumentException::new);
    }
}
