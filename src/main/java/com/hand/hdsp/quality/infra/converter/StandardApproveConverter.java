package com.hand.hdsp.quality.infra.converter;

import java.util.Optional;

import com.hand.hdsp.quality.api.dto.StandardApproveDTO;
import com.hand.hdsp.quality.domain.entity.StandardApprove;
import com.hand.hdsp.quality.infra.dataobject.StandardApproveDO;
import io.choerodon.core.convertor.ConvertorI;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

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