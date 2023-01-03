package com.hand.hdsp.quality.infra.converter;

import com.hand.hdsp.quality.api.dto.RootDicDTO;
import com.hand.hdsp.quality.domain.entity.RootDic;
import com.hand.hdsp.quality.infra.dataobject.RootDicDO;
import io.choerodon.core.convertor.ConvertorI;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * <p>转换器</p>
 *
 * @author guoliang.li01@hand-china.com 2022-12-06 14:35:46
 */
@Component
public class RootDicConverter implements ConvertorI<RootDic, RootDicDO, RootDicDTO> {

    @Override
    public RootDic dtoToEntity( RootDicDTO dto) {
        return Optional.ofNullable(dto)
                .map(o -> {
                    RootDic entity = RootDic.builder().build();
                    BeanUtils.copyProperties(dto, entity);
                    return entity;
                }).orElseThrow(IllegalArgumentException::new);
    }

    @Override
    public RootDicDTO entityToDto(RootDic entity) {

        return Optional.ofNullable(entity)
                .map(o -> {
                    RootDicDTO dto= RootDicDTO.builder().build();
                    BeanUtils.copyProperties(entity,dto);
                    return dto;
                }).orElseThrow(IllegalArgumentException::new);
    }
}