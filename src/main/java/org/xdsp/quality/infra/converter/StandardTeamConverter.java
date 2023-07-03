package org.xdsp.quality.infra.converter;

import io.choerodon.core.convertor.ConvertorI;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;
import org.xdsp.quality.api.dto.StandardTeamDTO;
import org.xdsp.quality.domain.entity.StandardTeam;
import org.xdsp.quality.infra.dataobject.StandardTeamDO;

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