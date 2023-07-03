package org.xdsp.quality.infra.converter;

import io.choerodon.core.convertor.ConvertorI;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;
import org.xdsp.quality.api.dto.RootMatchHisDTO;
import org.xdsp.quality.domain.entity.RootMatchHis;
import org.xdsp.quality.infra.dataobject.RootMatchHisDO;

import java.util.Optional;

/**
 * <p>字段标准匹配记录表转换器</p>
 *
 * @author shijie.gao@hand-china.com 2022-12-07 10:42:30
 */
@Component
public class RootMatchHisConverter implements ConvertorI<RootMatchHis, RootMatchHisDO, RootMatchHisDTO> {

    @Override
    public RootMatchHis dtoToEntity( RootMatchHisDTO dto) {
        return Optional.ofNullable(dto)
                .map(o -> {
                    RootMatchHis entity = RootMatchHis.builder().build();
                    BeanUtils.copyProperties(dto, entity);
                    return entity;
                }).orElseThrow(IllegalArgumentException::new);
    }

    @Override
    public RootMatchHisDTO entityToDto(RootMatchHis entity) {

        return Optional.ofNullable(entity)
                .map(o -> {
                    RootMatchHisDTO dto= RootMatchHisDTO.builder().build();
                    BeanUtils.copyProperties(entity,dto);
                    return dto;
                }).orElseThrow(IllegalArgumentException::new);
    }
}