package com.hand.hdsp.quality.infra.converter;

import com.hand.hdsp.quality.domain.entity.NameStandardHistoryHead;
import com.hand.hdsp.quality.infra.dataobject.NameStandardHistoryHeadDO;
import com.hand.hdsp.quality.api.dto.NameStandardHistoryHeadDTO;

import io.choerodon.core.convertor.ConvertorI;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


import java.util.Optional;

/**
 * <p>命名标准执行历史头表转换器</p>
 *
 * @author 张鹏 2020-11-26 11:11:24
 */
@Component
public class NameStandardHistoryHeadConverter implements ConvertorI<NameStandardHistoryHead, NameStandardHistoryHeadDO, NameStandardHistoryHeadDTO> {

    @Override
    public NameStandardHistoryHead dtoToEntity( NameStandardHistoryHeadDTO dto) {
        return Optional.ofNullable(dto)
                .map(o -> {
                    NameStandardHistoryHead entity = NameStandardHistoryHead.builder().build();
                    BeanUtils.copyProperties(dto, entity);
                    return entity;
                }).orElseThrow(IllegalArgumentException::new);
    }

    @Override
    public NameStandardHistoryHeadDTO entityToDto(NameStandardHistoryHead entity) {

        return Optional.ofNullable(entity)
                .map(o -> {
                    NameStandardHistoryHeadDTO dto= NameStandardHistoryHeadDTO.builder().build();
                    BeanUtils.copyProperties(entity,dto);
                    return dto;
                }).orElseThrow(IllegalArgumentException::new);
    }
}