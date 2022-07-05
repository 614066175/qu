package com.hand.hdsp.quality.infra.converter;

import com.hand.hdsp.quality.api.dto.StandardStatisticsDTO;
import com.hand.hdsp.quality.domain.entity.StandardStatistics;
import com.hand.hdsp.quality.infra.dataobject.StandardStatisticsDO;
import io.choerodon.core.convertor.ConvertorI;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * <p>标准落标表转换器</p>
 *
 * @author ZHENG.LI04@HAND-CHINA.COM 2022-05-17 15:20:56
 */
@Component
public class StandardStatisticsConverter implements ConvertorI<StandardStatistics, StandardStatisticsDO, StandardStatisticsDTO> {

    @Override
    public StandardStatistics dtoToEntity( StandardStatisticsDTO dto) {
        return Optional.ofNullable(dto)
                .map(o -> {
                    StandardStatistics entity = StandardStatistics.builder().build();
                    BeanUtils.copyProperties(dto, entity);
                    return entity;
                }).orElseThrow(IllegalArgumentException::new);
    }

    @Override
    public StandardStatisticsDTO entityToDto(StandardStatistics entity) {

        return Optional.ofNullable(entity)
                .map(o -> {
                    StandardStatisticsDTO dto= StandardStatisticsDTO.builder().build();
                    BeanUtils.copyProperties(entity,dto);
                    return dto;
                }).orElseThrow(IllegalArgumentException::new);
    }
}