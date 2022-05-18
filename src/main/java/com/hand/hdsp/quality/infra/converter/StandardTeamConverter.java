package com.hand.hdsp.quality.infra.converter;

import com.hand.hdsp.quality.domain.entity.StandardTeam;
import com.hand.hdsp.quality.infra.dataobject.StandardTeamDO;
import com.hand.hdsp.quality.api.dto.StandardTeamDTO;

import io.choerodon.core.convertor.ConvertorI;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


import java.util.Optional;

/**
 * <p>标准表转换器</p>
 *
 * @author guoliang.li01@hand-china.com 2022-05-09 15:50:16
 */
@Component
public class StandardTeamConverter implements ConvertorI<StandardTeam, StandardTeamDO, StandardTeamDTO> {

    @Override
    public StandardTeam dtoToEntity( StandardTeamDTO dto) {
        return Optional.ofNullable(dto)
                .map(o -> {
                    StandardTeam entity = StandardTeam.builder().build();
                    BeanUtils.copyProperties(dto, entity);
                    return entity;
                }).orElseThrow(IllegalArgumentException::new);
    }

    @Override
    public StandardTeamDTO entityToDto(StandardTeam entity) {

        return Optional.ofNullable(entity)
                .map(o -> {
                    StandardTeamDTO dto= StandardTeamDTO.builder().build();
                    BeanUtils.copyProperties(entity,dto);
                    return dto;
                }).orElseThrow(IllegalArgumentException::new);
    }
}