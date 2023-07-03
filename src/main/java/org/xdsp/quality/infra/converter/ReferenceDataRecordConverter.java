package org.xdsp.quality.infra.converter;

import io.choerodon.core.convertor.ConvertorI;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;
import org.xdsp.quality.api.dto.ReferenceDataRecordDTO;
import org.xdsp.quality.domain.entity.ReferenceDataRecord;
import org.xdsp.quality.infra.dataobject.ReferenceDataRecordDO;

import java.util.Optional;

/**
 * <p>参考数据工作流记录表转换器</p>
 *
 * @author fuqiang.luo@hand-china.com 2023-04-17 20:01:04
 */
@Component
public class ReferenceDataRecordConverter implements ConvertorI<ReferenceDataRecord, ReferenceDataRecordDO, ReferenceDataRecordDTO> {

    @Override
    public ReferenceDataRecord dtoToEntity( ReferenceDataRecordDTO dto) {
        return Optional.ofNullable(dto)
                .map(o -> {
                    ReferenceDataRecord entity = ReferenceDataRecord.builder().build();
                    BeanUtils.copyProperties(dto, entity);
                    return entity;
                }).orElseThrow(IllegalArgumentException::new);
    }

    @Override
    public ReferenceDataRecordDTO entityToDto(ReferenceDataRecord entity) {

        return Optional.ofNullable(entity)
                .map(o -> {
                    ReferenceDataRecordDTO dto= ReferenceDataRecordDTO.builder().build();
                    BeanUtils.copyProperties(entity,dto);
                    return dto;
                }).orElseThrow(IllegalArgumentException::new);
    }
}