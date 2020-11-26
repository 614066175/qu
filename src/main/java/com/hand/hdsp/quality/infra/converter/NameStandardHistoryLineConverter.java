package com.hand.hdsp.quality.infra.converter;

import com.hand.hdsp.quality.domain.entity.NameStandardHistoryLine;
import com.hand.hdsp.quality.infra.dataobject.NameStandardHistoryLineDO;
import com.hand.hdsp.quality.api.dto.NameStandardHistoryLineDTO;

import io.choerodon.core.convertor.ConvertorI;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


import java.util.Optional;

/**
 * <p>命名标准执行历史行表转换器</p>
 *
 * @author 张鹏 2020-11-26 11:11:24
 */
@Component
public class NameStandardHistoryLineConverter implements ConvertorI<NameStandardHistoryLine, NameStandardHistoryLineDO, NameStandardHistoryLineDTO> {

    @Override
    public NameStandardHistoryLine dtoToEntity( NameStandardHistoryLineDTO dto) {
        return Optional.ofNullable(dto)
                .map(o -> {
                    NameStandardHistoryLine entity = NameStandardHistoryLine.builder().build();
                    BeanUtils.copyProperties(dto, entity);
                    return entity;
                }).orElseThrow(IllegalArgumentException::new);
    }

    @Override
    public NameStandardHistoryLineDTO entityToDto(NameStandardHistoryLine entity) {

        return Optional.ofNullable(entity)
                .map(o -> {
                    NameStandardHistoryLineDTO dto= NameStandardHistoryLineDTO.builder().build();
                    BeanUtils.copyProperties(entity,dto);
                    return dto;
                }).orElseThrow(IllegalArgumentException::new);
    }
}