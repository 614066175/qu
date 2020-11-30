package com.hand.hdsp.quality.infra.converter;

import com.hand.hdsp.quality.domain.entity.NameExecHistory;
import com.hand.hdsp.quality.infra.dataobject.NameExecHistoryDO;
import com.hand.hdsp.quality.api.dto.NameExecHistoryDTO;

import io.choerodon.core.convertor.ConvertorI;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


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