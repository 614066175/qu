package com.hand.hdsp.quality.infra.converter;

import com.hand.hdsp.quality.domain.entity.WorkOrder;
import com.hand.hdsp.quality.infra.dataobject.WorkOrderDO;
import com.hand.hdsp.quality.api.dto.WorkOrderDTO;

import io.choerodon.core.convertor.ConvertorI;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


import java.util.Optional;

/**
 * <p>转换器</p>
 *
 * @author guoliang.li01@hand-china.com 2022-04-20 16:28:13
 */
@Component
public class WorkOrderConverter implements ConvertorI<WorkOrder, WorkOrderDO, WorkOrderDTO> {

    @Override
    public WorkOrder dtoToEntity( WorkOrderDTO dto) {
        return Optional.ofNullable(dto)
                .map(o -> {
                    WorkOrder entity = WorkOrder.builder().build();
                    BeanUtils.copyProperties(dto, entity);
                    return entity;
                }).orElseThrow(IllegalArgumentException::new);
    }

    @Override
    public WorkOrderDTO entityToDto(WorkOrder entity) {

        return Optional.ofNullable(entity)
                .map(o -> {
                    WorkOrderDTO dto= WorkOrderDTO.builder().build();
                    BeanUtils.copyProperties(entity,dto);
                    return dto;
                }).orElseThrow(IllegalArgumentException::new);
    }
}