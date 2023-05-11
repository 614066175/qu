package com.hand.hdsp.quality.infra.converter;

import com.hand.hdsp.quality.domain.entity.ReferenceDataHistory;
import com.hand.hdsp.quality.infra.dataobject.ReferenceDataHistoryDO;
import com.hand.hdsp.quality.api.dto.ReferenceDataHistoryDTO;

import io.choerodon.core.convertor.ConvertorI;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


import java.util.Optional;

/**
 * <p>参考数据头表转换器</p>
 *
 * @author fuqiang.luo@hand-china.com 2023-04-14 16:23:35
 */
@Component
public class ReferenceDataHistoryConverter implements ConvertorI<ReferenceDataHistory, ReferenceDataHistoryDO, ReferenceDataHistoryDTO> {

    @Override
    public ReferenceDataHistory dtoToEntity( ReferenceDataHistoryDTO dto) {
        return Optional.ofNullable(dto)
                .map(o -> {
                    ReferenceDataHistory entity = ReferenceDataHistory.builder().build();
                    BeanUtils.copyProperties(dto, entity);
                    return entity;
                }).orElseThrow(IllegalArgumentException::new);
    }

    @Override
    public ReferenceDataHistoryDTO entityToDto(ReferenceDataHistory entity) {

        return Optional.ofNullable(entity)
                .map(o -> {
                    ReferenceDataHistoryDTO dto= ReferenceDataHistoryDTO.builder().build();
                    BeanUtils.copyProperties(entity,dto);
                    return dto;
                }).orElseThrow(IllegalArgumentException::new);
    }
}