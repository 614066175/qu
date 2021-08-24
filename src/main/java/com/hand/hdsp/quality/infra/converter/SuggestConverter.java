package com.hand.hdsp.quality.infra.converter;

import com.hand.hdsp.quality.domain.entity.Suggest;
import com.hand.hdsp.quality.infra.dataobject.SuggestDO;
import com.hand.hdsp.quality.api.dto.SuggestDTO;

import io.choerodon.core.convertor.ConvertorI;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


import java.util.Optional;

/**
 * <p>问题知识库表转换器</p>
 *
 * @author lei.song@hand-china.com 2021-08-17 11:47:17
 */
@Component
public class SuggestConverter implements ConvertorI<Suggest, SuggestDO, SuggestDTO> {

    @Override
    public Suggest dtoToEntity( SuggestDTO dto) {
        return Optional.ofNullable(dto)
                .map(o -> {
                    Suggest entity = Suggest.builder().build();
                    BeanUtils.copyProperties(dto, entity);
                    return entity;
                }).orElseThrow(IllegalArgumentException::new);
    }

    @Override
    public SuggestDTO entityToDto(Suggest entity) {

        return Optional.ofNullable(entity)
                .map(o -> {
                    SuggestDTO dto= SuggestDTO.builder().build();
                    BeanUtils.copyProperties(entity,dto);
                    return dto;
                }).orElseThrow(IllegalArgumentException::new);
    }
}