package org.xdsp.quality.infra.converter;

import io.choerodon.core.convertor.ConvertorI;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;
import org.xdsp.quality.api.dto.BatchResultBaseDTO;
import org.xdsp.quality.domain.entity.BatchResultBase;
import org.xdsp.quality.infra.dataobject.BatchResultBaseDO;

import java.util.Optional;

/**
 * <p>批数据方案结果表-表信息转换器</p>
 *
 * @author feng.liu01@hand-china.com 2020-03-24 16:19:52
 */
@Component
public class BatchResultBaseConverter implements ConvertorI<BatchResultBase, BatchResultBaseDO, BatchResultBaseDTO> {

    @Override
    public BatchResultBase dtoToEntity(BatchResultBaseDTO dto) {
        return Optional.ofNullable(dto)
                .map(o -> {
                    BatchResultBase entity = BatchResultBase.builder().build();
                    BeanUtils.copyProperties(dto, entity);
                    return entity;
                }).orElseThrow(IllegalArgumentException::new);
    }

    @Override
    public BatchResultBaseDTO entityToDto(BatchResultBase entity) {

        return Optional.ofNullable(entity)
                .map(o -> {
                    BatchResultBaseDTO dto = BatchResultBaseDTO.builder().build();
                    BeanUtils.copyProperties(entity, dto);
                    return dto;
                }).orElseThrow(IllegalArgumentException::new);
    }
}
