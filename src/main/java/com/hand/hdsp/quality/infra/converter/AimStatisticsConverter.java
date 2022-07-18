package com.hand.hdsp.quality.infra.converter;

import com.hand.hdsp.quality.api.dto.AimStatisticsDTO;
import com.hand.hdsp.quality.domain.entity.AimStatistics;
import com.hand.hdsp.quality.infra.dataobject.AimStatisticsDO;
import io.choerodon.core.convertor.ConvertorI;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * <p>标准落标统计表转换器</p>
 *
 * @author guoliang.li01@hand-china.com 2022-06-16 14:38:20
 */
@Component
public class AimStatisticsConverter implements ConvertorI<AimStatistics, AimStatisticsDO, AimStatisticsDTO> {

    @Override
    public AimStatistics dtoToEntity(AimStatisticsDTO dto) {
        return Optional.ofNullable(dto)
                .map(o -> {
                    AimStatistics entity = AimStatistics.builder().build();
                    BeanUtils.copyProperties(dto, entity);
                    return entity;
                }).orElseThrow(IllegalArgumentException::new);
    }

    @Override
    public AimStatisticsDTO entityToDto(AimStatistics entity) {

        return Optional.ofNullable(entity)
                .map(o -> {
                    AimStatisticsDTO dto = AimStatisticsDTO.builder().build();
                    BeanUtils.copyProperties(entity, dto);
                    return dto;
                }).orElseThrow(IllegalArgumentException::new);
    }
}