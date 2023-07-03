package org.xdsp.quality.infra.converter;

import io.choerodon.core.convertor.ConvertorI;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;
import org.xdsp.quality.api.dto.StandardApproveDTO;
import org.xdsp.quality.domain.entity.StandardApprove;
import org.xdsp.quality.infra.dataobject.StandardApproveDO;

import java.util.Optional;

/**
 * <p>标准申请记录表转换器</p>
 *
 * @author guoliangli01.@hand-china.com 2020-11-25 17:03:12
 */
@Component
public class StandardApproveConverter implements ConvertorI<StandardApprove, StandardApproveDO, StandardApproveDTO> {

    @Override
    public StandardApprove dtoToEntity(StandardApproveDTO dto) {
        return Optional.ofNullable(dto)
                .map(o -> {
                    StandardApprove entity = StandardApprove.builder().build();
                    BeanUtils.copyProperties(dto, entity);
                    return entity;
                }).orElseThrow(IllegalArgumentException::new);
    }

    @Override
    public StandardApproveDTO entityToDto(StandardApprove entity) {

        return Optional.ofNullable(entity)
                .map(o -> {
                    StandardApproveDTO dto= StandardApproveDTO.builder().build();
                    BeanUtils.copyProperties(entity,dto);
                    return dto;
                }).orElseThrow(IllegalArgumentException::new);
    }
}