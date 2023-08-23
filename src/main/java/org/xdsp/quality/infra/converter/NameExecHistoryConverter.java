package org.xdsp.quality.infra.converter;

import io.choerodon.core.convertor.ConvertorI;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;
import org.xdsp.quality.api.dto.NameExecHistoryDTO;
import org.xdsp.quality.domain.entity.NameExecHistory;
import org.xdsp.quality.infra.dataobject.NameExecHistoryDO;

import java.util.Optional;

/**
 * <p>命名标准执行历史表转换器</p>
 *
 * @author 张鹏 2020-11-27 16:35:40
 */
@Component
public class NameExecHistoryConverter implements ConvertorI<NameExecHistory, NameExecHistoryDO, NameExecHistoryDTO> {

    @Override
    public NameExecHistory dtoToEntity( NameExecHistoryDTO dto) {
        return Optional.ofNullable(dto)
                .map(o -> {
                    NameExecHistory entity = NameExecHistory.builder().build();
                    BeanUtils.copyProperties(dto, entity);
                    return entity;
                }).orElseThrow(IllegalArgumentException::new);
    }

    @Override
    public NameExecHistoryDTO entityToDto(NameExecHistory entity) {

        return Optional.ofNullable(entity)
                .map(o -> {
                    NameExecHistoryDTO dto= NameExecHistoryDTO.builder().build();
                    BeanUtils.copyProperties(entity,dto);
                    return dto;
                }).orElseThrow(IllegalArgumentException::new);
    }
}