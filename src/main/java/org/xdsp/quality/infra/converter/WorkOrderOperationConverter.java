package org.xdsp.quality.infra.converter;

import io.choerodon.core.convertor.ConvertorI;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;
import org.xdsp.quality.api.dto.WorkOrderOperationDTO;
import org.xdsp.quality.domain.entity.WorkOrderOperation;
import org.xdsp.quality.infra.dataobject.WorkOrderOperationDO;

import java.util.Optional;

/**
 * <p>转换器</p>
 *
 * @author guoliang.li01@hand-china.com 2022-04-20 16:28:13
 */
@Component
public class WorkOrderOperationConverter implements ConvertorI<WorkOrderOperation, WorkOrderOperationDO, WorkOrderOperationDTO> {

    @Override
    public WorkOrderOperation dtoToEntity( WorkOrderOperationDTO dto) {
        return Optional.ofNullable(dto)
                .map(o -> {
                    WorkOrderOperation entity = WorkOrderOperation.builder().build();
                    BeanUtils.copyProperties(dto, entity);
                    return entity;
                }).orElseThrow(IllegalArgumentException::new);
    }

    @Override
    public WorkOrderOperationDTO entityToDto(WorkOrderOperation entity) {

        return Optional.ofNullable(entity)
                .map(o -> {
                    WorkOrderOperationDTO dto= WorkOrderOperationDTO.builder().build();
                    BeanUtils.copyProperties(entity,dto);
                    return dto;
                }).orElseThrow(IllegalArgumentException::new);
    }
}